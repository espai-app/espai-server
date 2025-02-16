package app.espai.businesslogic;

import app.espai.dao.Agencies;
import app.espai.dao.Attachments;
import app.espai.dao.LicenseConditions;
import app.espai.dao.Movies;
import app.espai.dao.ProductionMediums;
import app.espai.dao.ProductionTags;
import app.espai.dao.ProductionVersions;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.LicenseConditionFilter;
import app.espai.filter.ProductionMediumFilter;
import app.espai.filter.ProductionTagFilter;
import app.espai.filter.ProductionVersionFilter;
import app.espai.model.Agency;
import app.espai.model.Attachment;
import app.espai.model.LicenseCondition;
import app.espai.model.Movie;
import app.espai.model.ProductionMedium;
import app.espai.model.ProductionTag;
import app.espai.model.ProductionVersion;
import app.espai.webservice.AttachmentImport;
import app.espai.webservice.LicenseConditionImport;
import app.espai.webservice.MovieImport;
import app.espai.webservice.ProductionMediumImport;
import app.espai.webservice.ProductionTagImport;
import app.espai.webservice.ProductionVersionImport;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import jakarta.inject.Named;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
@Named
@Stateful
public class MovieImporter {

  @EJB
  private Movies movies;

  @EJB
  private ProductionTags tags;

  @EJB
  private ProductionVersions versions;

  @EJB
  private LicenseConditions conditions;

  @EJB
  private Agencies agencies;

  @EJB
  private ProductionMediums media;

  @EJB
  private Attachments attachments;

  public void importMovies(List<MovieImport> movieList) {
    for (MovieImport m : movieList) {
      Movie movie = importMovie(m);
      importTags(movie, m.getTags());
      importVersions(movie, m.getVersions());
      importConditions(movie, m.getConditions());
      importMedia(movie, m.getMedia());
      importAttachments(movie, m.getAttachments());
    }
  }

  private Movie importMovie(MovieImport source) {

    Movie target = null;

    if (source.getExternalId() != null && !source.getExternalId().isBlank()) {
      target = movies.getByExternalId(source.getExternalId());
    }

    if (target == null) {
      target = movies.getByTitle(source.getTitle());
    }

    if (target == null) {
      target = new Movie();
    }

    // copy data
    target.setExternalId(
            source.getExternalId() != null && !source.getExternalId().isBlank()
            ? source.getExternalId()
            : null);
    target.setTitle(replaceIfNotEmpty(source.getTitle(), target.getTitle()));
    target.setDescription(replaceIfNotEmpty(source.getDescription(), target.getDescription()));
    target.setDurationInMinutes(replaceIfNotEmpty(
            source.getDurationInMinutes(), target.getDurationInMinutes()));
    target.setRating(replaceIfNotEmpty(source.getRating(), target.getRating()));
    target.setFromAge(replaceIfNotEmpty(source.getFromAge(), target.getFromAge()));
    target.setToAge(replaceIfNotEmpty(source.getToAge(), target.getToAge()));
    target.setAgency(importAgency(source.getAgency()));
    target.setProductionCountries(replaceIfNotEmpty(
            source.getProductionCountries(), target.getProductionCountries()));
    target.setProductionYear(replaceIfNotEmpty(
            source.getProductionYear(), target.getProductionYear()));
    target.setReleaseDate(replaceIfNotEmpty(source.getReleaseDate(), target.getReleaseDate()));
    target.setDirector(replaceIfNotEmpty(source.getDirector(), target.getDirector()));
    target.setStarring(replaceIfNotEmpty(source.getStarring(), target.getStarring()));
    target.setBook(replaceIfNotEmpty(source.getBook(), target.getBook()));
    target.setInternalNote(replaceIfNotEmpty(source.getInternalNote(), target.getInternalNote()));

    return movies.save(target);
  }

  private Agency importAgency(String name) {

    if (name == null) {
      return null;
    }

    Agency a = agencies.getByName(name);
    if (a == null) {
      a = new Agency();
      a.setName(name);
      a = agencies.save(a);
    }

    return a;
  }

  private void importTags(Movie movie, List<ProductionTagImport> source) {

    if (source == null) {
      return;
    }

    ProductionTagFilter tagFilter = new ProductionTagFilter();
    tagFilter.setProduction(movie);

    List<ProductionTag> tagList = tags.list(tagFilter).getItems();
    int countSources = source.size();

    // overwrite existing and delete tags if count is higher than from import source
    for (int i = 0; i < tagList.size(); i++) {

      ProductionTag currentTag = tagList.get(i);

      if (i < countSources) {
        ProductionTagImport currentSource = source.get(i);
        currentTag.setCategory(currentSource.getCategory());
        currentTag.setValue(currentSource.getValue());
        tags.save(currentTag);
      } else {
        tags.delete(currentTag);
      }
    }

    // if import source has higher count, add new tags
    if (countSources > tagList.size()) {
      for (int i = tagList.size(); i < countSources; i++) {
        ProductionTagImport currentSource = source.get(i);
        ProductionTag currentTag = new ProductionTag(
                movie,
                currentSource.getCategory(),
                currentSource.getValue());
        tags.save(currentTag);
      }
    }
  }

  private void importVersions(Movie movie, List<ProductionVersionImport> source) {

    if (source == null) {
      return;
    }

    ProductionVersionFilter versionFilter = new ProductionVersionFilter();
    versionFilter.setProduction(movie);

    List<ProductionVersion> existingVersions = versions.list(versionFilter).getItems();

    // only add missing versions - deleting or overwriting versions might break existing reservations
    for (ProductionVersionImport s : source) {

      boolean found = false;
      for (ProductionVersion e : existingVersions) {
        if (Objects.equals(e.getMediaFormat(), s.getMediaFormat())
                && Objects.equals(e.getSpokenLanguage(), s.getSpokenLanguage())
                && Objects.equals(e.getSubtitles(), s.getSubtitles())) {
          found = true;
          break;
        }
      }

      if (!found) {
        ProductionVersion newVersion = new ProductionVersion();
        newVersion.setProduction(movie);
        newVersion.setVersionName(s.getVersionName());
        newVersion.setMediaFormat(s.getMediaFormat());
        newVersion.setSpokenLanguage(s.getSpokenLanguage());
        newVersion.setSubtitles(s.getSubtitles());

        versions.save(newVersion);
      }
    }
  }

  private void importConditions(Movie movie, List<LicenseConditionImport> source) {

    if (source == null) {
      return;
    }

    LicenseConditionFilter conditionFilter = new LicenseConditionFilter();
    conditionFilter.setProduction(movie);
    List<LicenseCondition> existingConditions = conditions.list(conditionFilter).getItems();

    // overwrite existing or create new license conditions
    for (LicenseConditionImport s : source) {
      LicenseCondition target = null;
      for (LicenseCondition c : existingConditions) {
        if (c.getAgency() != null && c.getAgency().getName().equals(s.getAgency())
                && c.isCommercial() == s.isCommercial()) {

          target = c;
          break;
        }
      }

      if (target == null) {
        target = new LicenseCondition();
        target.setProduction(movie);
        target.setAgency(importAgency(s.getAgency()));
      }

      // TODO change to application's default currency
      if (s.getGuarantee() != null) {
        target.setGuarantee(new Monetary(s.getGuarantee(), "EUR"));
      }

      if (s.getFixedPrice() != null) {
        target.setFixedPrice(new Monetary(s.getFixedPrice(), "EUR"));
      }

      if (s.getTicketShare() != null) {
        target.setTicketShare(new Monetary(s.getTicketShare(), "EUR"));
      }

      if (s.getTicketShare() != null) {
        target.setTicketShare(new Monetary(s.getTicketShare(), "EUR"));
      }

      if (s.getAdditionalCost() != null) {
        target.setAdditionalCost(new Monetary(s.getAdditionalCost(), "EUR"));
      }

      target.setNotes(s.getNotes());
      target.setCommercial(s.isCommercial());

      conditions.save(target);
    }
  }

  private void importMedia(Movie movie, List<ProductionMediumImport> source) {

    if (source == null) {
      return;
    }

    ProductionMediumFilter mediumFilter = new ProductionMediumFilter();
    mediumFilter.setProduction(movie);

    List<ProductionMedium> mediumList = media.list(mediumFilter).getItems();

    for (ProductionMediumImport s : source) {

      ProductionMedium target = null;

      for (ProductionMedium m : mediumList) {

        if (s.getName().equals(m.getName()) && s.getFormat().equals(m.getFormat())) {
          target = m;
          break;
        }
      }

      if (target == null) {
        target = new ProductionMedium();
        target.setName(s.getName());
        target.setProduction(movie);
        target.setFormat(s.getFormat());
      }

      target.setReference(s.getReference());
      target.setNote(s.getNote());

      media.save(target);
    }
  }

  private void importAttachments(Movie movie, List<AttachmentImport> source) {

    if (source == null) {
      return;
    }

    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityId(movie.getId());
    attachmentFilter.setEntityType(Movie.class.getSimpleName());

    List<Attachment> attachmentList = attachments.list(attachmentFilter).getItems();
    List<String> checksums = attachmentList.stream().map(Attachment::getChecksum).toList();

    for (AttachmentImport a : source) {
      if (checksums.contains(a.getChecksum())) {
        continue;
      }

      try {
        Attachment target = new Attachment();
        target.setEntityId(movie.getId());
        target.setEntityType(Movie.class.getSimpleName());
        target.setCaption(a.getCaption());
        target.setCategory(a.getCategory());
        target.setCopyright(a.getCopyright());
        target.setChecksum(a.getChecksum());
        target.setMediaType(a.getType());

        target = attachments.save(target);
        attachments.setDataStream(target, new URL(a.getReferenceUrl()).openStream());
      } catch (IOException ex) {
        Logger.getLogger(MovieImporter.class.getName()).log(Level.SEVERE,
                String.format("Error importing attachment for %s.", movie.getTitle()), ex);
      }
    }

  }

  private String replaceIfNotEmpty(String value, String defaultValue) {
    if (value != null && !value.isBlank()) {
      return value;
    }
    return defaultValue;
  }

  private Integer replaceIfNotEmpty(Integer value, Integer defaultValue) {
    if (value != null) {
      return value;
    }
    return defaultValue;
  }

  private LocalDate replaceIfNotEmpty(LocalDate value, LocalDate defaultValue) {
    if (value != null) {
      return value;
    }
    return defaultValue;
  }

}
