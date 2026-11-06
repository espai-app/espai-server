package app.espai.sdk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "Attachment",
        description = "Represents a generic attachment (poster, still, downloadable content...).")
public class AttachmentDTO {

  private Long id;

  @NotNull(message = "mediaType is a required field")
  @NotBlank(message = "mediaType must not be empty")
  @Size(max = 100, message = "mediaType must be between 0 to 100 characters")
  private String mediaType;

  @Size(max = 100, message = "mimeType must be between 0 to 100 characters")
  private String mimeType;

  @NotNull(message = "category is a required field")
  @NotBlank(message = "category must not be empty")
  @Size(max = 100, message = "name must be between 0 to 100 characters")
  private String category;
  private Integer position;

  @NotNull(message = "caption is a required field")
  @NotBlank(message = "caption must not be blank")
  @Size(max = 200, message = "caption must be between 0 to 100 characters")
  private String caption;

  @Size(max = 300, message = "location must be between 0 to 100 characters")
  private String location;

  @Size(max = 300, message = "copyright must be between 0 to 100 characters")
  private String copyright;

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
   * @return the mediaType
   */
  public String getMediaType() {
    return mediaType;
  }

  /**
   * @param mediaType the mediaType to set
   */
  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  /**
   * @return the mimeType
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * @param mimeType the mimeType to set
   */
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * @return the category
   */
  public String getCategory() {
    return category;
  }

  /**
   * @param category the category to set
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * @return the position
   */
  public Integer getPosition() {
    return position;
  }

  /**
   * @param position the position to set
   */
  public void setPosition(Integer position) {
    this.position = position;
  }

  /**
   * @return the caption
   */
  public String getCaption() {
    return caption;
  }

  /**
   * @param caption the caption to set
   */
  public void setCaption(String caption) {
    this.caption = caption;
  }

  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * @param location the location to set
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * @return the copyright
   */
  public String getCopyright() {
    return copyright;
  }

  /**
   * @param copyright the copyright to set
   */
  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }
//</editor-fold>

}
