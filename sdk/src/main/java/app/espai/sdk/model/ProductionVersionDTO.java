
package app.espai.sdk.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "ProductionVersion",
        description = "Represents a language version for a production.")
public class ProductionVersionDTO {

  private Long id;
  private String versionName;
  private String mediaFormat;
  private String spokenLanguage;
  private boolean audioDescription;
  private String subtitles;
  private boolean closedCaption;

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
   * @return the versionName
   */
  public String getVersionName() {
    return versionName;
  }

  /**
   * @param versionName the versionName to set
   */
  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

  /**
   * @return the mediaFormat
   */
  public String getMediaFormat() {
    return mediaFormat;
  }

  /**
   * @param mediaFormat the mediaFormat to set
   */
  public void setMediaFormat(String mediaFormat) {
    this.mediaFormat = mediaFormat;
  }

  /**
   * @return the spokenLanguage
   */
  public String getSpokenLanguage() {
    return spokenLanguage;
  }

  /**
   * @param spokenLanguage the spokenLanguage to set
   */
  public void setSpokenLanguage(String spokenLanguage) {
    this.spokenLanguage = spokenLanguage;
  }

  /**
   * @return the audioDescription
   */
  public boolean isAudioDescription() {
    return audioDescription;
  }

  /**
   * @param audioDescription the audioDescription to set
   */
  public void setAudioDescription(boolean audioDescription) {
    this.audioDescription = audioDescription;
  }

  /**
   * @return the subtitles
   */
  public String getSubtitles() {
    return subtitles;
  }

  /**
   * @param subtitles the subtitles to set
   */
  public void setSubtitles(String subtitles) {
    this.subtitles = subtitles;
  }

  /**
   * @return the closedCaption
   */
  public boolean isClosedCaption() {
    return closedCaption;
  }

  /**
   * @param closedCaption the closedCaption to set
   */
  public void setClosedCaption(boolean closedCaption) {
    this.closedCaption = closedCaption;
  }
  //</editor-fold>
}
