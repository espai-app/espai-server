package app.espai.views.seasons;

import app.espai.dao.Events;
import app.espai.dao.SeasonVenues;
import app.espai.filter.SeasonVenueFilter;
import app.espai.model.SeasonVenue;
import app.espai.model.Venue;
import app.espai.model.VenueEventStatistic;
import app.espai.views.SeasonContext;
import app.espai.views.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class SeasonVenueIndex {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private UserContext userContext;

  @EJB
  private Events events;

  @EJB
  private SeasonVenues venues;

  private List<VenueEventStatistic> venueList;

  @PostConstruct
  public void init() {

    List<Venue> accessibleVenues;
    if (userContext.isRestricted()) {
      accessibleVenues = userContext.getVenues();
    } else {
      SeasonVenueFilter filter = new SeasonVenueFilter();
      filter.setSeason(seasonContext.getCurrentSeason());
      accessibleVenues = venues.list(filter).getItems().stream()
              .map(SeasonVenue::getVenue)
              .collect(Collectors.toList());
    }

    venueList = events.getNumberOfEvents(accessibleVenues, seasonContext.getCurrentSeason());
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the venueList
   */
  public List<VenueEventStatistic> getVenueList() {
    return venueList;
  }

  /**
   * @param venueList the venueList to set
   */
  public void setVenueList(List<VenueEventStatistic> venueList) {
    this.venueList = venueList;
  }
  //</editor-fold>

}
