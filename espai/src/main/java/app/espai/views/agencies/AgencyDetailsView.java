/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.agencies;

import app.espai.RequestParameters;
import app.espai.dao.Agencies;
import app.espai.dao.LicenseConditions;
import app.espai.filter.AgencyFilter;
import app.espai.filter.LicenseConditionFilter;
import app.espai.model.Agency;
import app.espai.model.LicenseCondition;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class AgencyDetailsView extends BaseView {
  
  @EJB
  private Agencies agencies;
  
  @EJB
  private LicenseConditions licenses;
  
  private Agency agency;
  private Agency targetAgency;
  
  private List<Agency> agencyList;
  private List<LicenseCondition> licenseList;
  
  @PostConstruct
  public void init() {
    
    Long agencyId = RequestParameters.getParameterAsLong("agencyId");
    if (agencyId == null) {
      throw new BadRequestException("agencyId is missing.");
    }
    
    agency = agencies.get(agencyId);
    if (agency == null) {
      throw new NotFoundException("Agency not found.");
    }
    
    AgencyFilter agencyFilter = new AgencyFilter();
    agencyFilter.setOrderBy("name");
    agencyFilter.setOrder(PageableFilter.Order.ASC);
    agencyList = agencies.list(agencyFilter).getItems();
    
    LicenseConditionFilter licenceFilter = new LicenseConditionFilter();
    licenceFilter.setAgency(agency);
    
    licenseList = licenses.list(licenceFilter).getItems();
    licenseList.sort((l1, l2) -> String.CASE_INSENSITIVE_ORDER.compare(
            l1.getProduction().getTitle(), l2.getProduction().getTitle()));
  }
  
  public void delete() {
    agencies.delete(agency);
    redirect("index.xhtml", "Lizenzgeber gelöscht.", null, FacesMessage.SEVERITY_INFO);
  }
  
  public void mergeWith() {
    agencies.merge(agency, targetAgency);
    redirect(String.format("details.xhtml?agencyId=%d", targetAgency.getId()), 
            "Lizenzgeber zusammengeführt.", 
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

  /**
   * @return the targetAgency
   */
  public Agency getTargetAgency() {
    return targetAgency;
  }

  /**
   * @param targetAgency the targetAgency to set
   */
  public void setTargetAgency(Agency targetAgency) {
    this.targetAgency = targetAgency;
  }

  /**
   * @return the agencyList
   */
  public List<Agency> getAgencyList() {
    return agencyList;
  }

  /**
   * @param agencyList the agencyList to set
   */
  public void setAgencyList(List<Agency> agencyList) {
    this.agencyList = agencyList;
  }

  /**
   * @return the licenseList
   */
  public List<LicenseCondition> getLicenseList() {
    return licenseList;
  }

  /**
   * @param licenseList the licenseList to set
   */
  public void setLicenseList(List<LicenseCondition> licenseList) {
    this.licenseList = licenseList;
  }  
  //</editor-fold>
  
}
