package app.espai.sdk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name="Production", description = "Represents a movie, play, discussion...")
public class ProductionDTO {

  private Long id;
  private ProductionTypeDTO productionType;

  @NotNull(message = "title is a required field")
  @NotBlank(message = "title must not be blank")
  @Size(max = 200, message = "title must be between 0 to 200 characters")
  private String title;

  @Size(max = 200, message = "subtitle must be between 0 to 200 characters")
  private String subtitle;
  private String description;
  private Integer durationInMinutes;
  private Integer rating;
  private Integer recommendedAge;

  private List<ProductionMetaValueDTO> metadata;
  private List<ProductionVersionDTO> versions;
  private List<AttachmentDTO> attachments;
  private List<LicenseConditionDTO> licenseConditions;

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
   * @return the productionType
   */
  public ProductionTypeDTO getProductionType() {
    return productionType;
  }

  /**
   * @param productionType the productionType to set
   */
  public void setProductionType(ProductionTypeDTO productionType) {
    this.productionType = productionType;
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
   * @return the subtitle
   */
  public String getSubtitle() {
    return subtitle;
  }

  /**
   * @param subtitle the subtitle to set
   */
  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
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
   * @return the recommendedAge
   */
  public Integer getRecommendedAge() {
    return recommendedAge;
  }

  /**
   * @param recommendedAge the recommendedAge to set
   */
  public void setRecommendedAge(Integer recommendedAge) {
    this.recommendedAge = recommendedAge;
  }

  /**
   * @return the metadata
   */
  public List<ProductionMetaValueDTO> getMetadata() {
    return metadata;
  }

  /**
   * @param metadata the metadata to set
   */
  public void setMetadata(List<ProductionMetaValueDTO> metadata) {
    this.metadata = metadata;
  }

  /**
   * @return the versions
   */
  public List<ProductionVersionDTO> getVersions() {
    return versions;
  }

  /**
   * @param versions the versions to set
   */
  public void setVersions(List<ProductionVersionDTO> versions) {
    this.versions = versions;
  }

  /**
   * @return the attachments
   */
  public List<AttachmentDTO> getAttachments() {
    return attachments;
  }

  /**
   * @param attachments the attachments to set
   */
  public void setAttachments(List<AttachmentDTO> attachments) {
    this.attachments = attachments;
  }

  /**
   * @return the licenseConditions
   */
  public List<LicenseConditionDTO> getLicenseConditions() {
    return licenseConditions;
  }

  /**
   * @param licenseConditions the licenseConditions to set
   */
  public void setLicenseConditions(List<LicenseConditionDTO> licenseConditions) {
    this.licenseConditions = licenseConditions;
  }
  //</editor-fold>
}
