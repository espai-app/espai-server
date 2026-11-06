package app.espai.dto;

import app.espai.dao.ProductionMetaFields;
import app.espai.dao.ProductionTypes;
import app.espai.filter.ProductionMetaFieldFilter;
import app.espai.model.ProductionMetaField;
import app.espai.model.ProductionType;
import app.espai.sdk.model.ProductionTypeDTO;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class ProductionTypeDTOConverter {

  @EJB
  private ProductionTypes types;

  @EJB
  private ProductionMetaFields fields;

  @Inject
  private ProductionMetaFieldDTOConverter fieldConverter;

  public ProductionTypeDTO toShortDTO(ProductionType type) {
    ProductionTypeDTO result = new ProductionTypeDTO();
    result.setId(type.getId());
    result.setName(type.getName());

    return result;
  }

  public ProductionTypeDTO toDTO(ProductionType type) {
    ProductionTypeDTO result = new ProductionTypeDTO();
    result.setId(type.getId());
    result.setName(type.getName());

    ProductionMetaFieldFilter fieldFilter = new ProductionMetaFieldFilter();
    fieldFilter.setProductionType(type);
    List<ProductionMetaField> fieldList = fields.list(fieldFilter).getItems();

    result.setMetaFields(fieldList.stream().map(f -> fieldConverter.toDTO(f)).toList());

    return result;
  }

  public ProductionType fromDTO(ProductionTypeDTO type, boolean readonly) {

    if (type == null) {
      return null;
    }

    ProductionType result = null;

    if (type.getId() != null) {
      result = types.get(type.getId());
    }

    if (readonly) {
      return result;
    }

    if (result == null) {
      result = new ProductionType();
    }

    result.setName(type.getName());

    return result;
  }
}
