/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.productionType;

import app.espai.dao.ProductionTypes;
import app.espai.filter.ProductionTypeFilter;
import app.espai.model.ProductionType;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class ProductionTypeIndexView implements Serializable {
  
  @Inject
  private ProductionTypes types;
  
  private List<ProductionType> typeList;
  
  private boolean showInactive;
  
  @PostConstruct
  public void init() {
    ProductionTypeFilter filter = new ProductionTypeFilter();
    filter.setActive(Boolean.TRUE);
    filter.setOrderBy("name");
    filter.setOrder(PageableFilter.Order.ASC);
    typeList = types.list(filter).getItems();
  }
  
  public void onActiveChange(ValueChangeEvent ev) {
    ProductionTypeFilter filter = new ProductionTypeFilter();
    if (!((boolean) ev.getNewValue())) {
      filter.setActive(Boolean.TRUE);
    }
    
    typeList = types.list(filter).getItems();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the typeList
   */
  public List<ProductionType> getTypeList() {
    return typeList;
  }
  
  /**
   * @param typeList the typeList to set
   */
  public void setTypeList(List<ProductionType> typeList) {
    this.typeList = typeList;
  }
  
  /**
   * @return the showInactive
   */
  public boolean isShowInactive() {
    return showInactive;
  }
  
  /**
   * @param showInactive the showInactive to set
   */
  public void setShowInactive(boolean showInactive) {
    this.showInactive = showInactive;
  }
  //</editor-fold>
  
}
