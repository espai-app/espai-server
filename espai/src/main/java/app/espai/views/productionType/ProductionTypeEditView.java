/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.productionType;

import app.espai.RequestParameters;
import app.espai.dao.ProductionMetaFields;
import app.espai.dao.ProductionTypes;
import app.espai.filter.ProductionMetaFieldFilter;
import app.espai.model.ProductionMetaField;
import app.espai.model.ProductionType;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.NotFoundException;
import java.io.Serializable;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class ProductionTypeEditView extends BaseView implements Serializable {

  @Inject
  private ProductionTypes types;
  
  @Inject
  private ProductionMetaFields fields;
  
  private ProductionType type;
  private List<ProductionMetaField> fieldList;
  
  @PostConstruct
  public void init() {
    Long productionTypeId = RequestParameters.getParameterAsLong("productionTypeId");
    if (productionTypeId != null) {
      type = types.get(productionTypeId);
      
      if (type == null) {
        throw new NotFoundException("productionType not found.");
      }
      
      ProductionMetaFieldFilter fieldFilter = new ProductionMetaFieldFilter();
      fieldFilter.setProductionType(type);
      fieldFilter.setOrderBy("position");
      fieldFilter.setOrder(PageableFilter.Order.ASC);
      
      fieldList = fields.list(fieldFilter).getItems();
    } else {
      type = new ProductionType();
      type.setActive(true);
    }
  }
  
  public void save() {
    type = types.save(type);
    redirect(String.format("editor.xhtml?productionTypeId=%d", type.getId()), 
            "Veranstaltungsformat gespeichert.", 
            null, 
            FacesMessage.SEVERITY_INFO);
  }
  
  public void deleteField(Long fieldId) {
    ProductionMetaField field = fields.get(fieldId);
    if (field != null) {
      fields.delete(field);
    }
    
    init();
  }
  
  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the type
   */
  public ProductionType getType() {
    return type;
  }
  
  /**
   * @param type the type to set
   */
  public void setType(ProductionType type) {
    this.type = type;
  }
  
  /**
   * @return the fieldList
   */
  public List<ProductionMetaField> getFieldList() {
    return fieldList;
  }
  
  /**
   * @param fieldList the fieldList to set
   */
  public void setFieldList(List<ProductionMetaField> fieldList) {
    this.fieldList = fieldList;
  }
  //</editor-fold>
  
}
