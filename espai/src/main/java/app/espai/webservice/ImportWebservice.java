package app.espai.webservice;

import app.espai.VisionKinoImportFilter;
import app.espai.dao.Agencies;
import app.espai.dao.Attachments;
import app.espai.dao.Movies;
import app.espai.dao.ProductionTags;
import app.espai.dao.ProductionVersions;
import app.espai.filter.AgencyFilter;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.MovieFilter;
import app.espai.filter.ProductionTagFilter;
import app.espai.filter.ProductionVersionFilter;
import app.espai.model.Agency;
import app.espai.model.Attachment;
import app.espai.model.AttachmentType;
import app.espai.model.Movie;
import app.espai.model.ProductionTag;
import app.espai.model.ProductionVersion;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rocks.xprs.runtime.db.PageableList;

/**
 *
 * @author rborowski
 */
@Path("import")
public class ImportWebservice {

  @Inject
  private Movies movies;

  @Inject
  private ProductionVersions versions;

  @Inject
  private Agencies agencies;

  @Inject
  private Attachments attachments;

  @Inject
  private ProductionTags tags;

  @GET
  @Path("visionKino")
  public void importVisionKino(@QueryParam("ignore") Integer ignore) throws IOException {

    ignore = ignore != null ? ignore : 0;
    VisionKinoImportFilter filter = new VisionKinoImportFilter();
    List<String> movieUrls = filter.getMovies();

    movieUrls = movieUrls.subList(ignore, movieUrls.size());

    for (String url : movieUrls) {
      importSingleMovie(url);

      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        Logger.getLogger(ImportWebservice.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  @GET
  @Path("visionKinoSingle")
  public void importSingleMovie(@QueryParam("url") String url) throws IOException {

    VisionKinoImportFilter filter = new VisionKinoImportFilter();
    VisionKinoImportFilter.MovieWithRelations m = filter.parseMovieDetailsPage(url);
    Logger.getLogger(ImportWebservice.class.getName()).log(Level.INFO, "Getting {0}", url);

    Movie movie = m.getMovie();

    // check if Agency already exists
    if (movie.getAgency() != null) {
      AgencyFilter agencyFilter = new AgencyFilter();
      agencyFilter.setName(movie.getAgency().getName());

      PageableList<Agency> agencyList = agencies.list(agencyFilter);
      Agency agency;
      if (agencyList.getItems().isEmpty()) {
        agency = agencies.save(movie.getAgency());
      } else {
        agency = agencyList.getItems().get(0);
      }

      movie.setAgency(agency);
    }

    // check if movie already exists
    MovieFilter movieFilter = new MovieFilter();
    movieFilter.setTitle(m.getMovie().getTitle());
    movieFilter.setProductionYear(m.getMovie().getProductionYear());

    PageableList<Movie> existingMovies = movies.list(movieFilter);
    if (existingMovies.getTotalNumberOfResults() > 0) {
      movie.setId(existingMovies.getItems().get(0).getId());
    }

    movie = movies.save(movie);

    // overwrite all tags
    ProductionTagFilter tagFilter = new ProductionTagFilter();
    tagFilter.setProduction(movie);

    for (ProductionTag t : tags.list(tagFilter).getItems()) {
      tags.delete(t);
    }

    for (ProductionTag t : m.getTags()) {
      t.setProduction(movie);
      tags.save(t);
    }

    // overwrite all attachments
    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityId(movie.getId());
    attachmentFilter.setEntityType(Movie.class.getSimpleName());

    for (Attachment a : attachments.list(attachmentFilter).getItems()) {
      attachments.delete(a);
    }

    for (Attachment a : m.getAttachments()) {
      a.setEntityId(movie.getId());
      a.setEntityType(Movie.class.getSimpleName());
      attachments.save(a);
    }
  }

  @GET
  @Path("createVersions")
  public void createVersions() throws IOException {
    List<? extends Movie> movieList = movies.list().getItems();
    ProductionVersionFilter filter = new ProductionVersionFilter();

    for (Movie m : movieList) {

      filter.setProduction(m);
      if (versions.list(filter).getTotalNumberOfResults() == 0) {
        ProductionVersion version = new ProductionVersion();
        version.setProduction(m);
        version.setVersionName("");
        version.setSpokenLanguage("deu");

        versions.save(version);
      }
    }

  }

  @GET
  @Path("downloadAttachments")
  public void downloadAttachments() throws IOException, InterruptedException {

    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setMediaType(AttachmentType.IMAGE);

    List<Attachment> attachmentList = attachments.list(attachmentFilter).getItems();
    for (Attachment a : attachmentList) {
      if (a.getLocation() != null) {
        try {
        Thread.sleep(1000);
        URLConnection connection = new URL(a.getLocation()).openConnection();
        String contentType = connection.getHeaderField("Content-Type");
        attachments.setDataStream(a, connection.getInputStream());
        a.setMimeType(contentType);
        a.setLocation(null);
        attachments.save(a);
        } catch (IOException ex) {
          Logger.getLogger(ImportWebservice.class.getName()).log(Level.SEVERE, "", ex);
        }
      }
    }
  }

}
