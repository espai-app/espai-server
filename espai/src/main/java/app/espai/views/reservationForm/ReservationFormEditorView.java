/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.reservationForm;

import app.espai.RequestParameters;
import app.espai.dao.ReservationFormFields;
import app.espai.dao.ReservationForms;
import app.espai.filter.ReservationFormFieldFilter;
import app.espai.model.ReservationForm;
import app.espai.model.ReservationFormField;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class ReservationFormEditorView extends BaseView {
  
  @EJB
  private ReservationForms reservationForms;
  
  @EJB
  private ReservationFormFields formFields;
  
  private ReservationForm form;
  private List<ReservationFormField> formFieldList;
  
  @PostConstruct
  public void init() {
    
    Long formId = RequestParameters.getParameterAsLong("reservationFormId");
    if (formId != null) {
      form = reservationForms.get(formId);
      
      if (form == null) {
        throw new NotFoundException();
      }
      
      reloadFields();
    } else {
      form = new ReservationForm();
    }
  }
  
  public void save() {
    form = reservationForms.save(form);
    
    redirect(
            String.format("editor.xhtml?reservationFormId=%d", form.getId()), 
            "Formular gespeichert.", 
            null, 
            FacesMessage.SEVERITY_INFO);
  }
  
  public void deleteField(long id) {
    
    ReservationFormField field = formFields.get(id);
    if (field != null) {
      formFields.delete(field);
    }
    
    reloadFields();
  }
  
  private void reloadFields() {
    ReservationFormFieldFilter filter = new ReservationFormFieldFilter();
    filter.setReservationForm(form);
    filter.setOrderBy("position");
    filter.setOrder(PageableFilter.Order.ASC);
    
    formFieldList = formFields.list(filter).getItems();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the form
   */
  public ReservationForm getForm() {
    return form;
  }
  
  /**
   * @param form the form to set
   */
  public void setForm(ReservationForm form) {
    this.form = form;
  }
  
  /**
   * @return the formFieldList
   */
  public List<ReservationFormField> getFormFieldList() {
    return formFieldList;
  }
  
  /**
   * @param formFieldList the formFieldList to set
   */
  public void setFormFieldList(List<ReservationFormField> formFieldList) {
    this.formFieldList = formFieldList;
  }
  //</editor-fold>
  
}
