package app.espai.views.venues;

import app.espai.dao.Halls;
import app.espai.dao.Venues;
import app.espai.model.Hall;
import app.espai.model.Venue;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import rocks.xprs.runtime.exceptions.InvalidDataException;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class HallEditorView {

  @EJB
  private Venues venues;

  @EJB
  private Halls halls;

  private Hall hall;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String hallIdParam = econtext.getRequestParameterMap().get("hallId");
    if (hallIdParam == null || !hallIdParam.matches("\\d+")) {

      String venueId = econtext.getRequestParameterMap().get("venueId");
      if (venueId == null || !venueId.matches("\\d+")) {
        throw new InvalidDataException("venueId missing.");
      }

      Venue venue = venues.get(Long.parseLong(venueId));
      if (venue == null) {
        throw new ResourceNotFoundException("Venue not found.");
      }

      hall = new Hall();
      hall.setVenue(venue);
    } else {
      hall = halls.get(Long.parseLong(hallIdParam));
    }
  }

  public void save() {
    halls.save(hall);
    PrimeFaces.current().dialog().closeDynamic(hall);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the hall
   */
  public Hall getHall() {
    return hall;
  }

  /**
   * @param hall the hall to set
   */
  public void setHall(Hall hall) {
    this.hall = hall;
  }
  //</editor-fold>
}
