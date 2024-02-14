package app.espai.views.seasons;

import app.espai.dao.SeasonVenues;
import app.espai.dao.Seasons;
import app.espai.dao.Venues;
import app.espai.filter.SeasonVenueFilter;
import app.espai.model.Season;
import app.espai.model.SeasonVenue;
import app.espai.model.Venue;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class SeasonAddVenueDialog implements Serializable {

  @EJB
  private Seasons seasons;

  @EJB
  private Venues venues;

  @EJB
  private SeasonVenues seasonVenues;

  private Season season;
  private List<? extends Venue> venueList;
  private List<Venue> selectedVenues;

  @PostConstruct
  public void init() {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    season = seasons.get(Long.parseLong(externalContext.getRequestParameterMap().get("seasonId")));

    SeasonVenueFilter svFilter = new SeasonVenueFilter();
    svFilter.setSeason(season);
    List<SeasonVenue> currentProductions = seasonVenues.list(svFilter).getItems();
    List<Long> currentIds = currentProductions
            .stream()
            .map(p -> p.getVenue().getId())
            .toList();

    venueList = venues.list()
            .getItems()
            .stream()
            .filter(i -> {return !currentIds.contains(i.getId());})
            .sorted(((p1, p2) -> String.CASE_INSENSITIVE_ORDER.compare(
                    p1.getCity() + p1.getName(), p2.getCity() + p2.getName())))
            .toList();
  }

  public void addSelected() {
    for (Venue v : selectedVenues) {
      SeasonVenue s = new SeasonVenue();
      s.setSeason(season);
      s.setVenue(v);

      seasonVenues.save(s);
    }

     PrimeFaces.current().dialog().closeDynamic(null);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the season
   */
  public Season getSeason() {
    return season;
  }

  /**
   * @param season the season to set
   */
  public void setSeason(Season season) {
    this.season = season;
  }

  /**
   * @return the venueList
   */
  public List<? extends Venue> getVenueList() {
    return venueList;
  }

  /**
   * @param venueList the venueList to set
   */
  public void setVenueList(List<? extends Venue> venueList) {
    this.venueList = venueList;
  }

  /**
   * @return the selectedVenues
   */
  public List<Venue> getSelectedVenues() {
    return selectedVenues;
  }

  /**
   * @param selectedVenues the selectedVenues to set
   */
  public void setSelectedVenues(List<Venue> selectedVenues) {
    this.selectedVenues = selectedVenues;
  }
  //</editor-fold>
}
