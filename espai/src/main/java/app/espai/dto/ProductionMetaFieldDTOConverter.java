
package app.espai.dto;

import app.espai.dao.ProductionMetaFields;
import app.espai.model.ProductionMetaField;
import app.espai.sdk.model.FieldType;
import app.espai.sdk.model.ProductionMetaFieldDTO;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class ProductionMetaFieldDTOConverter {

  @EJB
  private ProductionMetaFields metaFields;

  public ProductionMetaFieldDTO toDTO(ProductionMetaField metaField) {

    if (metaField == null) {
      return null;
    }

    ProductionMetaFieldDTO result = new ProductionMetaFieldDTO();
    result.setId(metaField.getId());
    result.setFieldType(FieldType.valueOf(metaField.getFieldType().toString()));
    result.setName(metaField.getName());
    result.setLabel(metaField.getLabel());
    result.setPosition(metaField.getPosition());

    return result;
  }

  public ProductionMetaField fromDTO(ProductionMetaFieldDTO field, boolean readonly) {

    if (field == null) {
      return null;
    }

    ProductionMetaField result = null;
    if (field.getId() != null) {
      result = metaFields.get(field.getId());
    }

    if (readonly) {
      return result;
    }

    if (result == null) {
      result = new ProductionMetaField();
    }

    result.setFieldType(app.espai.model.FieldType.valueOf(field.getFieldType().toString()));
    result.setName(field.getName());
    result.setLabel(field.getLabel());
    result.setPosition(field.getPosition());

    return result;
  }
}
