/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.reservationForm;

import app.espai.dao.ReservationForms;
import app.espai.filter.ReservationFormFilter;
import app.espai.model.ReservationForm;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class ReservationFormIndexView implements Serializable {
  
  @EJB
  private ReservationForms reservationForms;
  
  private List<ReservationForm> formList;
  private boolean showInactive;
  
  @PostConstruct
  public void init() {
    
    ReservationFormFilter filter = new ReservationFormFilter();
    filter.setActive(true);
    filter.setOrderBy("name");
    filter.setOrder(PageableFilter.Order.ASC);
    
    formList = reservationForms.list(filter).getItems();
  }
  
  public void onActiveChange(ValueChangeEvent ev) {
    ReservationFormFilter filter = new ReservationFormFilter();
    if (!((boolean) ev.getNewValue())) {
      filter.setActive(Boolean.TRUE);
    }
    
    formList = reservationForms.list(filter).getItems();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the formList
   */
  public List<ReservationForm> getFormList() {
    return formList;
  }
  
  /**
   * @param formList the formList to set
   */
  public void setFormList(List<ReservationForm> formList) {
    this.formList = formList;
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
