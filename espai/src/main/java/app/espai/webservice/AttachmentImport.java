package app.espai.webservice;

import app.espai.model.AttachmentType;

/**
 *
 * @author rborowski
 */
public class AttachmentImport {

  private AttachmentType type;
  private String category;
  private String caption;
  private String copyright;
  private String referenceUrl;
  private boolean download;
  private String checksum;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the type
   */
  public AttachmentType getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(AttachmentType type) {
    this.type = type;
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

  /**
   * @return the referenceUrl
   */
  public String getReferenceUrl() {
    return referenceUrl;
  }

  /**
   * @param referenceUrl the referenceUrl to set
   */
  public void setReferenceUrl(String referenceUrl) {
    this.referenceUrl = referenceUrl;
  }

  /**
   * @return the download
   */
  public boolean isDownload() {
    return download;
  }

  /**
   * @param download the download to set
   */
  public void setDownload(boolean download) {
    this.download = download;
  }

  /**
   * @return the checksum
   */
  public String getChecksum() {
    return checksum;
  }

  /**
   * @param checksum the checksum to set
   */
  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }
  //</editor-fold>


}
