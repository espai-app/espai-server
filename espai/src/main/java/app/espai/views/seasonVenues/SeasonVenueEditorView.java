/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.seasonVenues;

import app.espai.dao.SeasonVenues;
import app.espai.model.SeasonVenue;
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
public class SeasonVenueEditorView {

  @EJB
  private SeasonVenues seasonVenues;

  private SeasonVenue seasonVenue;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String seasonVenueIdParam = econtext.getRequestParameterMap().get("seasonVenueId");

    if (seasonVenueIdParam == null || !seasonVenueIdParam.matches("\\d+")) {
      throw new RuntimeException("seasonVenueId is missing.");
    }

    seasonVenue = seasonVenues.get(Long.parseLong(seasonVenueIdParam));
  }

  public void save() {
    seasonVenues.save(seasonVenue);
    PrimeFaces.current().dialog().closeDynamic(null);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the seasonVenue
   */
  public SeasonVenue getSeasonVenue() {
    return seasonVenue;
  }

  /**
   * @param seasonVenue the seasonVenue to set
   */
  public void setSeasonVenue(SeasonVenue seasonVenue) {
    this.seasonVenue = seasonVenue;
  }
  //</editor-fold>

}
