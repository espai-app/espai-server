
package app.espai.sdk.model;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "ProductionType",
        description = "Represents type of the production (theater play, movie, discussion, workshop...).")
public class ProductionTypeDTO {

  private Long id;
  private String name;
  private List<ProductionMetaFieldDTO> metaFields;

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
   * @return the metaFields
   */
  public List<ProductionMetaFieldDTO> getMetaFields() {
    return metaFields;
  }

  /**
   * @param metaFields the metaFields to set
   */
  public void setMetaFields(List<ProductionMetaFieldDTO> metaFields) {
    this.metaFields = metaFields;
  }
  //</editor-fold>
}
