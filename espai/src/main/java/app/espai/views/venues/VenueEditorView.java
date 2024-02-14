package app.espai.views.venues;

import app.espai.dao.Venues;
import app.espai.model.Venue;
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
public class VenueEditorView {

  @EJB
  private Venues venues;

  private Venue venue;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String venueIdParam = econtext.getRequestParameterMap().get("venueId");
    if (venueIdParam == null || !venueIdParam.matches("\\d+")) {
      venue = new Venue();
    } else {
      venue = venues.get(Long.parseLong(venueIdParam));
    }
  }

  public void save() {
    venue = venues.save(venue);
    PrimeFaces.current().dialog().closeDynamic(venue.getId());
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the venue
   */
  public Venue getVenue() {
    return venue;
  }

  /**
   * @param venue the venue to set
   */
  public void setVenue(Venue venue) {
    this.venue = venue;
  }
  //</editor-fold>

}
