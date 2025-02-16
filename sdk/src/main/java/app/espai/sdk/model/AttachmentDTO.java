package app.espai.sdk.model;

/**
 *
 * @author rborowski
 */
public class AttachmentDTO {

  private Long id;
  private String mediaType;
  private String mimeType;
  private Integer position;
  private String caption;
  private String location;
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
