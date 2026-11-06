/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.reservationForm;

import app.espai.RequestParameters;
import app.espai.dao.ReservationFormFields;
import app.espai.dao.ReservationForms;
import app.espai.model.ReservationForm;
import app.espai.model.ReservationFormField;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
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
public class ReservationFormFieldEditorView extends BaseView implements Serializable {
  
  @EJB
  private ReservationForms forms;
  
  @EJB
  private ReservationFormFields formFields;
  
  private ReservationForm form;
  private ReservationFormField field;
  
  @PostConstruct
  public void init() {
    
    Long formId = RequestParameters.getParameterAsLong("reservationFormId");
    Long fieldId = RequestParameters.getParameterAsLong("reservationFormFieldId");
    
    if (fieldId != null) {
      field = formFields.get(fieldId);
      
      if (field == null) {
        throw new NotFoundException();
      }
    } else if (formId != null) {
      
      form = forms.get(formId);
      if (form == null) {
        throw new NotFoundException();
      }
      
      field = new ReservationFormField();
      field.setReservationForm(form);
    } else {
      throw new BadRequestException();
    }
  }
  
  public void save() {
    field = formFields.save(field);
    
    redirect(String.format("editor.xhtml?reservationFormId=%d", field.getReservationForm().getId()),
            "Feld gespeichert.",
            null,
            FacesMessage.SEVERITY_INFO);
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
   * @return the field
   */
  public ReservationFormField getField() {
    return field;
  }
  
  /**
   * @param field the field to set
   */
  public void setField(ReservationFormField field) {
    this.field = field;
  }
  //</editor-fold>
  
}
