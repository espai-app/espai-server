package app.espai.sdk.model;

import java.util.List;
import java.util.Map;

/**
 *
 * @author rborowski
 */
public class ProductionDTO {

  private Long id;
  private String title;
  private String description;
  private Integer durationInMinutes;
  private Integer fromAge;
  private Integer toAge;
  private Integer rating;

  private Map<String, List<String>> tags;
  private Map<String, List<AttachmentDTO>> attachments;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
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
   * @return the tags
   */
  public Map<String, List<String>> getTags() {
    return tags;
  }

  /**
   * @param tags the tags to set
   */
  public void setTags(Map<String, List<String>> tags) {
    this.tags = tags;
  }

  /**
   * @return the attachments
   */
  public Map<String, List<AttachmentDTO>> getAttachments() {
    return attachments;
  }

  /**
   * @param attachments the attachments to set
   */
  public void setAttachments(Map<String, List<AttachmentDTO>> attachments) {
    this.attachments = attachments;
  }
  //</editor-fold>
}
