/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.agencies;

import app.espai.RequestParameters;
import app.espai.dao.Agencies;
import app.espai.model.Agency;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import jakarta.ws.rs.NotFoundException;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class AgencyEditorView extends BaseView {

  @EJB
  private Agencies agencies;
  
  private Agency agency;
  
  @PostConstruct
  public void init() {
    
    Long agencyId = RequestParameters.getParameterAsLong("agencyId");
    if (agencyId == null) {
      agency = new Agency();
    } else {
      
      agency = agencies.get(agencyId);
      if (agency == null) {
        throw new NotFoundException("Agency not found.");
      }
    }
  }
  
  public void save() {
    agency = agencies.save(agency);
    redirect(String.format("details.xhtml?agencyId=%d", 
            agency.getId()), 
            "Lizenzgeber gespeichert.", 
            null, 
            FacesMessage.SEVERITY_INFO);
  }
  
  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the agency
   */
  public Agency getAgency() {
    return agency;
  }
  
  /**
   * @param agency the agency to set
   */
  public void setAgency(Agency agency) {
    this.agency = agency;
  }
  //</editor-fold>
  
}
