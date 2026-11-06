/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.seasonVenues;

import app.espai.dao.SeasonVenues;
import app.espai.dao.Venues;
import app.espai.model.SeasonVenue;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class SeasonVenueEditorView extends BaseView {

  @EJB
  private Venues venues;

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
    venues.save(seasonVenue.getVenue());
    seasonVenue = seasonVenues.save(seasonVenue);

    redirect(String.format("details.xhtml?seasonId=%d&seasonVenueId=%d",
            seasonVenue.getSeason().getId(), seasonVenue.getId()),
            "Änderungen gespeichert.",
            null,
            FacesMessage.SEVERITY_INFO);
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
