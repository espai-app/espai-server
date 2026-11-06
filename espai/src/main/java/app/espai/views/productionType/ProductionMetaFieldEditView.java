/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.productionType;

import app.espai.RequestParameters;
import app.espai.dao.ProductionMetaFields;
import app.espai.dao.ProductionTypes;
import app.espai.model.ProductionMetaField;
import app.espai.model.ProductionType;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.io.Serializable;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class ProductionMetaFieldEditView extends BaseView implements Serializable {
  
  @Inject
  private ProductionTypes types;
  
  @Inject
  private ProductionMetaFields fields;
  
  private ProductionMetaField field;
  
  @PostConstruct
  public void init() {
    
    Long typeId = RequestParameters.getParameterAsLong("productionTypeId");
    Long fieldId = RequestParameters.getParameterAsLong("productionMetaFieldId");
    
    if (fieldId != null) {
      field = fields.get(fieldId);
      
      if (field == null) {
        throw new NotFoundException("ProductionMetaField not found.");
      }
    } else {
      if (typeId == null ) {
        throw new BadRequestException("productionTypeId missing.");
      } else {
        ProductionType type = types.get(typeId);
        
        if (type == null) {
          throw new NotFoundException("ProductionType not found.");
        }
        
        field = new ProductionMetaField();
        field.setProductionType(type);
      }
    }
  }
  
  public void save() {
    field = fields.save(field);
    
    redirect(String.format("editor.xhtml?productionTypeId=%d", field.getProductionType().getId()), 
            "Feld gepeichert.", 
            null, 
            FacesMessage.SEVERITY_INFO);
  }
  
  public void selectFieldType() {
    System.out.println(field.getName());
  }
  
  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the field
   */
  public ProductionMetaField getField() {
    return field;
  }
  
  /**
   * @param field the field to set
   */
  public void setField(ProductionMetaField field) {
    this.field = field;
  }
  //</editor-fold>
  
}
