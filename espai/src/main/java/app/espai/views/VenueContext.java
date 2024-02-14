package app.espai.views;

import app.espai.Constants;
import app.espai.dao.Venues;
import app.espai.model.Venue;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 *
 * @author rborowski
 */
@Named
@Stateless
public class VenueContext {

  @EJB
  private UserContext userContext;

  @EJB
  private Venues venues;

  public Venue getCurrentVenue() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String venueId = econtext.getRequestParameterMap().get(Constants.VENUE_ID);

    if (venueId != null && venueId.matches("\\d+")) {
      Venue venue = venues.get(Long.parseLong(venueId));

      if (userContext.isRestricted() && !userContext.getVenues().contains(venue)) {
        return null;
      }

      return venue;
    }

    return null;
  }

}
