package app.espai.webservice;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rborowski
 */
public class MovieImport {

  private String externalId;
  private String title;
  private String description;
  private Integer durationInMinutes;
  private Integer rating;
  private Integer fromAge;
  private Integer toAge;
  private String agency;
  private String productionCountries;
  private String productionYear;
  private LocalDate releaseDate;
  private String director;
  private String starring;
  private String book;
  private String internalNote;

  private List<ProductionTagImport> tags;
  private List<ProductionVersionImport> versions;
  private List<LicenseConditionImport> conditions;
  private List<ProductionMediumImport> media;
  private List<AttachmentImport> attachments;

  public void addAttachment(AttachmentImport attachment) {
    if (attachments == null) {
      attachments = new LinkedList<>();
    }
    attachments.add(attachment);
  }

  public void addTag(ProductionTagImport tag) {
    if (tags == null) {
      tags = new LinkedList<>();
    }
    tags.add(tag);
  }

  public void addVersion(ProductionVersionImport version) {
    if (versions == null) {
      versions = new LinkedList<>();
    }
    versions.add(version);
  }

  public void addLicenseCondition(LicenseConditionImport condition) {
    if (conditions == null) {
      conditions = new LinkedList<>();
    }
    conditions.add(condition);
  }

  public void addMedium(ProductionMediumImport medium) {
    if (media == null) {
      media = new LinkedList<>();
    }
    media.add(medium);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the externalId
   */
  public String getExternalId() {
    return externalId;
  }

  /**
   * @param externalId the externalId to set
   */
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the durationInMinutes
   */
  public Integer getDurationInMinutes() {
    return durationInMinutes;
  }

  /**
   * @param durationInMinutes the durationInMinutes to set
   */
  public void setDurationInMinutes(Integer durationInMinutes) {
    this.durationInMinutes = durationInMinutes;
  }

  /**
   * @return the rating
   */
  public Integer getRating() {
    return rating;
  }

  /**
   * @param rating the rating to set
   */
  public void setRating(Integer rating) {
    this.rating = rating;
  }

  /**
   * @return the fromAge
   */
  public Integer getFromAge() {
    return fromAge;
  }

  /**
   * @param fromAge the fromAge to set
   */
  public void setFromAge(Integer fromAge) {
    this.fromAge = fromAge;
  }

  /**
   * @return the toAge
   */
  public Integer getToAge() {
    return toAge;
  }

  /**
   * @param toAge the toAge to set
   */
  public void setToAge(Integer toAge) {
    this.toAge = toAge;
  }

  /**
   * @return the agency
   */
  public String getAgency() {
    return agency;
  }

  /**
   * @param agency the agency to set
   */
  public void setAgency(String agency) {
    this.agency = agency;
  }

  /**
   * @return the productionCountries
   */
  public String getProductionCountries() {
    return productionCountries;
  }

  /**
   * @param productionCountries the productionCountries to set
   */
  public void setProductionCountries(String productionCountries) {
    this.productionCountries = productionCountries;
  }

  /**
   * @return the productionYear
   */
  public String getProductionYear() {
    return productionYear;
  }

  /**
   * @param productionYear the productionYear to set
   */
  public void setProductionYear(String productionYear) {
    this.productionYear = productionYear;
  }

  /**
   * @return the releaseDate
   */
  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  /**
   * @param releaseDate the releaseDate to set
   */
  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * @return the director
   */
  public String getDirector() {
    return director;
  }

  /**
   * @param director the director to set
   */
  public void setDirector(String director) {
    this.director = director;
  }

  /**
   * @return the starring
   */
  public String getStarring() {
    return starring;
  }

  /**
   * @param starring the starring to set
   */
  public void setStarring(String starring) {
    this.starring = starring;
  }

  /**
   * @return the book
   */
  public String getBook() {
    return book;
  }

  /**
   * @param book the book to set
   */
  public void setBook(String book) {
    this.book = book;
  }

  /**
   * @return the internalNote
   */
  public String getInternalNote() {
    return internalNote;
  }

  /**
   * @param internalNote the internalNote to set
   */
  public void setInternalNote(String internalNote) {
    this.internalNote = internalNote;
  }

  /**
   * @return the tags
   */
  public List<ProductionTagImport> getTags() {
    return tags;
  }

  /**
   * @param tags the tags to set
   */
  public void setTags(List<ProductionTagImport> tags) {
    this.tags = tags;
  }

  /**
   * @return the versions
   */
  public List<ProductionVersionImport> getVersions() {
    return versions;
  }

  /**
   * @param versions the versions to set
   */
  public void setVersions(List<ProductionVersionImport> versions) {
    this.versions = versions;
  }

  /**
   * @return the conditions
   */
  public List<LicenseConditionImport> getConditions() {
    return conditions;
  }

  /**
   * @param conditions the conditions to set
   */
  public void setConditions(List<LicenseConditionImport> conditions) {
    this.conditions = conditions;
  }

  /**
   * @return the media
   */
  public List<ProductionMediumImport> getMedia() {
    return media;
  }

  /**
   * @param media the media to set
   */
  public void setMedia(List<ProductionMediumImport> media) {
    this.media = media;
  }

  /**
   * @return the attachments
   */
  public List<AttachmentImport> getAttachments() {
    return attachments;
  }

  /**
   * @param attachments the attachments to set
   */
  public void setAttachments(List<AttachmentImport> attachments) {
    this.attachments = attachments;
  }
  //</editor-fold>

}
