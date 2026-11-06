
package app.espai.sdk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "ProductionMetaField",
        description = "Represents a type description for a production's custom field.")
public class ProductionMetaFieldDTO {

  private Long id;

  @NotNull(message = "name is a required field")
  @NotBlank(message = "name must not be blank")
  @Size(max = 200, message = "name must be between 0 to 200 characters")
  private String name;

  @NotNull(message = "label is a required field")
  @NotBlank(message = "label must not be blank")
  @Size(max = 200, message = "label must be between 0 to 200 characters")
  private String label;
  private Integer position;

  @NotNull(message = "fieldType is a required field")
  private FieldType fieldType;

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
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
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
   * @return the fieldType
   */
  public FieldType getFieldType() {
    return fieldType;
  }

  /**
   * @param fieldType the fieldType to set
   */
  public void setFieldType(FieldType fieldType) {
    this.fieldType = fieldType;
  }
  //</editor-fold>

}
