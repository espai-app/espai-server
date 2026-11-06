/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.licenseConditions;

import app.espai.RequestParameters;
import app.espai.dao.Agencies;
import app.espai.dao.LicenseConditions;
import app.espai.dao.Productions;
import app.espai.filter.AgencyFilter;
import app.espai.model.Agency;
import app.espai.model.LicenseCondition;
import app.espai.model.Production;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.primefaces.PF;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class LicenseConditionEditView {
  
  @EJB
  private Agencies agencies;
  
  @EJB
  private LicenseConditions licenses;
  
  @EJB
  private Productions productions;
  
  private LicenseCondition license;
  private List<Agency> agencyList;
  
  @PostConstruct
  public void init() {
    Long licenseId = RequestParameters.getParameterAsLong("licenseId");
    
    if (licenseId != null) {
      license = licenses.get(licenseId);
      
      if (license == null) {
        throw new NotFoundException("License not found.");
      }
    } else {
      Long productionId = RequestParameters.getParameterAsLong("productionId");
      
      if (productionId == null) {
        throw new BadRequestException("Production missing.");
      }
      
      Production production = productions.get(productionId);
      if (production == null) {
        throw new NotFoundException("Production not found.");
      }
      
      license = new LicenseCondition();
      license.setProduction(production);
    }
    
    if (license.getGuarantee() == null) {
        license.setGuarantee(new Monetary(BigDecimal.ZERO, "EUR"));
      }

      if (license.getFixedPrice() == null) {
        license.setFixedPrice(new Monetary(BigDecimal.ZERO, "EUR"));
      }

      if (license.getAdditionalCost() == null) {
        license.setAdditionalCost(new Monetary(BigDecimal.ZERO, "EUR"));
      }
    
    AgencyFilter agencyFilter = new AgencyFilter();
    agencyFilter.setOrderBy("name");
    agencyFilter.setOrder(AgencyFilter.Order.ASC);
    agencyList = agencies.list(agencyFilter).getItems();
  }
  
  public void save() {
    licenses.save(license);
    
    PF.current().dialog().closeDynamic(null);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the license
   */
  public LicenseCondition getLicense() {
    return license;
  }
  
  /**
   * @param license the license to set
   */
  public void setLicense(LicenseCondition license) {
    this.license = license;
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
  //</editor-fold>
  
}
