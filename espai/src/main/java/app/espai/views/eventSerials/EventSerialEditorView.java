/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.eventSerials;

import app.espai.dao.EventSerials;
import app.espai.dao.Seasons;
import app.espai.model.EventSerial;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class EventSerialEditorView {
  
  @EJB
  private EventSerials eventSerials;
  
  @EJB
  private Seasons seasons;
  
  private EventSerial eventSerial;
  
  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    
    String eventSerialIdParam = econtext.getRequestParameterMap().get("eventSerialId");
    String seasonIdParam = econtext.getRequestParameterMap().get("seasonId");
    
    if (eventSerialIdParam != null && eventSerialIdParam.matches("\\d+")) {
      eventSerial = eventSerials.get(Long.parseLong(eventSerialIdParam));
    } else if (seasonIdParam != null && seasonIdParam.matches("\\d+")) {
      eventSerial = new EventSerial();
      eventSerial.setSeason(seasons.get(Long.parseLong(seasonIdParam)));
    }
    
    if (getEventSerial() == null) {
      throw new RuntimeException("No eventSerial given.");
    }    
  }
  
  public void save() {
    eventSerials.save(eventSerial);
    
    PrimeFaces.current().dialog().closeDynamic(eventSerial);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the eventSerial
   */
  public EventSerial getEventSerial() {
    return eventSerial;
  }
  
  /**
   * @param eventSerial the eventSerial to set
   */
  public void setEventSerial(EventSerial eventSerial) {
    this.eventSerial = eventSerial;
  }
  //</editor-fold>
}
