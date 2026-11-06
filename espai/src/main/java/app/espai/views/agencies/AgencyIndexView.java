/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.agencies;

import app.espai.dao.Agencies;
import app.espai.filter.AgencyFilter;
import app.espai.model.Agency;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class AgencyIndexView {

  @EJB
  private Agencies agencies;
  
  private List<Agency> agencyList;
  
  @PostConstruct
  public void init() {
    AgencyFilter agencyFilter = new AgencyFilter();
    agencyFilter.setOrderBy("name");
    agencyFilter.setOrder(PageableFilter.Order.ASC);
    
    agencyList = agencies.list(agencyFilter).getItems();
  }
  
  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
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
