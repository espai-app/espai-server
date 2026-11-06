
package app.espai.sdk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "ProductionMetaValue",
        description = "Represents a custom meta value for a production.")
public class ProductionMetaValueDTO {

  private Long id;

  @NotNull(message = "productionMetaField is a required field")
  private ProductionMetaFieldDTO productionMetaField;

  @NotNull(message = "value is a required field")
  @NotBlank(message = "value must not be blank")
  @Size(max = 1000, message = "value must be between 0 to 1000 characters")
  private String value;

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
   * @return the productionMetaField
   */
  public ProductionMetaFieldDTO getProductionMetaField() {
    return productionMetaField;
  }

  /**
   * @param productionMetaField the productionMetaField to set
   */
  public void setProductionMetaField(ProductionMetaFieldDTO productionMetaField) {
    this.productionMetaField = productionMetaField;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }
  //</editor-fold>
}
