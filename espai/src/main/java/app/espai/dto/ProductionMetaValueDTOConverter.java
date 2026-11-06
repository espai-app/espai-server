
package app.espai.dto;

import app.espai.dao.ProductionMetaValues;
import app.espai.model.Production;
import app.espai.model.ProductionMetaValue;
import app.espai.sdk.model.ProductionMetaValueDTO;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class ProductionMetaValueDTOConverter {

  @EJB
  private ProductionMetaValues metaValues;

  @Inject
  private ProductionMetaFieldDTOConverter metaFieldConverter;

  public ProductionMetaValueDTO toDTO(ProductionMetaValue metaValue) {

    if (metaValue == null) {
      return null;
    }

    ProductionMetaValueDTO result = new ProductionMetaValueDTO();
    result.setId(metaValue.getId());
    result.setProductionMetaField(metaFieldConverter.toDTO(metaValue.getProductionMetaField()));
    result.setValue(metaValue.getValue());

    return result;
  }

  public ProductionMetaValue fromDTO(ProductionMetaValueDTO value,
          Production production, boolean readonly) {

    if (value == null) {
      return null;
    }

    ProductionMetaValue result = null;
    if (value.getId() != null) {
      result = metaValues.get(value.getId());
    }

    if (readonly) {
      return result;
    }

    if (result == null) {
      result = new ProductionMetaValue();
    }

    result.setProduction(production);
    result.setProductionMetaField(metaFieldConverter.fromDTO(value.getProductionMetaField(), true));
    result.setValue(value.getValue());

    return result;
  }
}
