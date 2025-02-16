package app.espai.views.seasons;

import app.espai.dao.Events;
import app.espai.dao.Halls;
import app.espai.dao.SeasonVenues;
import app.espai.filter.EventFilter;
import app.espai.filter.HallFilter;
import app.espai.filter.SeasonVenueFilter;
import app.espai.model.Event;
import app.espai.model.Hall;
import app.espai.model.SeasonVenue;
import app.espai.views.Dialog;
import app.espai.views.SeasonContext;
import app.espai.views.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DialogFrameworkOptions;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class SeasonVenueIndexView {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private UserContext userContext;

  @EJB
  private Events events;

  @EJB
  private Halls halls;

  @EJB
  private SeasonVenues seasonVenues;

  private List<SeasonVenue> venueList;
  private List<SeasonVenue> selectedVenues;
  private Map<Long, Long> eventCounterMap;

  @PostConstruct
  public void init() {

    SeasonVenueFilter filter = new SeasonVenueFilter();
    if (userContext.isRestricted()) {
      filter.setVenues(userContext.getVenues());
    }

    filter.setSeason(seasonContext.getCurrentSeason());
    venueList = seasonVenues.list(filter).getItems();
    venueList.sort((v1, v2) -> {return String.CASE_INSENSITIVE_ORDER.compare(
            v1.getVenue().getCity() + v1.getVenue().getName(),
            v2.getVenue().getCity() + v2.getVenue().getName());});

    HallFilter hallFilter = new HallFilter();
    hallFilter.setVenues(venueList.stream().map(SeasonVenue::getVenue).distinct().toList());
    List<Hall> hallList = halls.list(hallFilter).getItems();

    EventFilter eventFilter = new EventFilter();
    eventFilter.setHalls(hallList);
    eventFilter.setSeason(seasonContext.getCurrentSeason());
    List<Event> eventList = events.list(eventFilter).getItems();

    eventCounterMap = eventList.stream().
            collect(Collectors.groupingBy(
                    e -> e.getHall().getVenue().getId(),
                    Collectors.counting()));
  }

  public void addVenues() {
    DialogFrameworkOptions options = DialogFrameworkOptions.builder()
            .headerElement("Spielstätten hinzufügen")
            .width("900px")
            .height("630px")
            .contentWidth("100%")
            .modal(true)
            .responsive(true)
            .resizable(true)
            .build();

    Map<String, List<String>> params = new HashMap<>();
    params.put("seasonId", Arrays.asList(String.valueOf(seasonContext.getCurrentSeason().getId())));

    PrimeFaces.current().dialog().openDynamic("addVenues", options, params);
  }

  public void editSeasonVenue(long seasonVenueId) {
    Map<String, List<String>> params = new HashMap<>();
    params.put("seasonVenueId", Arrays.asList(String.valueOf(seasonVenueId)));

    PrimeFaces.current().dialog().openDynamic(
            "seasonVenueEditor",
            Dialog.getDefaultOptions(600, 300),
            params);
  }

  public void deleteVenues() {
    for (ListIterator<SeasonVenue> i = selectedVenues.listIterator(); i.hasNext();) {
      seasonVenues.delete(i.next());
      i.remove();
    }
    init();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the venueList
   */
  public List<SeasonVenue> getVenueList() {
    return venueList;
  }

  /**
   * @param venueList the venueList to set
   */
  public void setVenueList(List<SeasonVenue> venueList) {
    this.venueList = venueList;
  }

  /**
   * @return the selectedVenues
   */
  public List<SeasonVenue> getSelectedVenues() {
    return selectedVenues;
  }

  /**
   * @param selectedVenues the selectedVenues to set
   */
  public void setSelectedVenues(List<SeasonVenue> selectedVenues) {
    this.selectedVenues = selectedVenues;
  }

  /**
   * @return the eventCounterMap
   */
  public Map<Long, Long> getEventCounterMap() {
    return eventCounterMap;
  }

  /**
   * @param eventCounterMap the eventCounterMap to set
   */
  public void setEventCounterMap(Map<Long, Long> eventCounterMap) {
    this.eventCounterMap = eventCounterMap;
  }
  //</editor-fold>

}
