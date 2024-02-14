package app.espai.views.events;

import app.espai.dao.Events;
import app.espai.dao.Halls;
import app.espai.dao.ProductionVersions;
import app.espai.dao.SeasonProductions;
import app.espai.dao.SeasonVenues;
import app.espai.filter.EventFilter;
import app.espai.filter.HallFilter;
import app.espai.filter.ProductionVersionFilter;
import app.espai.filter.SeasonProductionFilter;
import app.espai.filter.SeasonVenueFilter;
import app.espai.model.Event;
import app.espai.model.Hall;
import app.espai.model.ProductionVersion;
import app.espai.model.Season;
import app.espai.model.SeasonProduction;
import app.espai.model.SeasonVenue;
import app.espai.model.Venue;
import app.espai.views.BaseView;
import app.espai.views.SeasonContext;
import app.espai.views.UserContext;
import app.espai.views.VenueContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import rocks.xprs.runtime.exceptions.InvalidDataException;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class ProgrammingView extends BaseView implements Serializable {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private VenueContext venueContext;

  @EJB
  private SeasonVenues seasonVenus;

  @EJB
  private UserContext userContext;

  @EJB
  private Halls halls;

  @EJB
  private Events events;

  @EJB
  private ProductionVersions productionVersions;

  @EJB
  private SeasonProductions productions;

  private Season currentSeason;
  private Venue currentVenue;
  private List<Venue> venueList;
  private List<Event> eventList = new LinkedList();
  private List<Event> selectedEvents;
  private List<ProductionVersion> productionVersionList;
  private List<Hall> hallList;
  private boolean keepDate;

  @PostConstruct
  public void init() {
    currentSeason = seasonContext.getCurrentSeason();
    currentVenue = venueContext.getCurrentVenue();

    if (currentSeason == null) {
      throw new InvalidDataException("Keine Spielstätten ausgewählt.");
    }

    SeasonVenueFilter venueFilter = new SeasonVenueFilter();
    venueFilter.setSeason(currentSeason);
    if (userContext.isRestricted()) {
      venueFilter.setVenues(userContext.getVenues());
    }

    List<SeasonVenue> seasonVenueList = seasonVenus.list(venueFilter).getItems();
    venueList = seasonVenueList
            .stream()
            .map(SeasonVenue::getVenue)
            .sorted((v1, v2) -> String.CASE_INSENSITIVE_ORDER.compare(
            v1.getCity() + v1.getName(), v2.getCity() + v2.getName()))
            .collect(Collectors.toList());

    if (venueList.isEmpty()) {
      throw new InvalidDataException("Keine Spielstätten ausgewählt.");
    }

    if (currentVenue == null) {
      currentVenue = venueList.get(0);
    }

    HallFilter hallFilter = new HallFilter();
    hallFilter.setVenue(currentVenue);

    hallList = halls.list(hallFilter).getItems();
    hallList.sort(Halls.DEFAULT_ORDER);

    EventFilter eventFilter = new EventFilter();
    eventFilter.setSeason(seasonContext.getCurrentSeason());
    eventFilter.setHalls(hallList);

    eventList.addAll(events.list(eventFilter).getItems());
    eventList.sort((Event o1, Event o2) -> {
      LocalDateTime t1 = LocalDateTime.of(o1.getDate(), o1.getTime());
      LocalDateTime t2 = LocalDateTime.of(o2.getDate(), o2.getTime());
      return t1.compareTo(t2);
    });

    SeasonProductionFilter productionFilter = new SeasonProductionFilter();
    productionFilter.setSeason(currentSeason);
    List<SeasonProduction> productionList = productions.list(productionFilter).getItems();

    ProductionVersionFilter versionFilter = new ProductionVersionFilter();
    versionFilter.setProductions(productionList
            .stream()
            .map(SeasonProduction::getProduction)
            .collect(Collectors.toList()));
    productionVersionList = productionVersions.list(versionFilter).getItems();
    productionVersionList.sort((p1, p2) -> String.CASE_INSENSITIVE_ORDER.compare(
            p1.getProduction().getTitle() + p1.getVersionName(),
            p2.getProduction().getTitle() + p2.getVersionName()));
  }

  public void onVenueChange() throws IOException {
    FacesContext.getCurrentInstance().getExternalContext()
            .redirect("?seasonId=" + currentSeason.getId().toString() + "&venueId=" + currentVenue.getId().toString());
  }

  public void addEvents(int amount) {
    LocalDate dateToSet = null;
    if (keepDate && !eventList.isEmpty()) {
      dateToSet = eventList.get(eventList.size() - 1).getDate();
    }

    for (int i = 0; i < amount; i++) {
      Event event = new Event();
      event.setSeason(currentSeason);
      event.setDate(dateToSet);
      eventList.add(event);
    }
  }

  public void deleteEvent(int index) {
    if (!eventList.isEmpty() && index >= 0 && index < eventList.size()) {
      Event s = eventList.get(index);
      if (s.getId() != null) {
        events.delete(s);
      }
      eventList.remove(index);
      System.out.println("Deleted " + s.getProduction().getProduction().getTitle());
      for (Event e : eventList) {
        System.out.println(e.getProduction().getProduction().getTitle());
      }
    }
  }

  public void save() {
    for (Event e : eventList) {
      if (e.getDate() != null && e.getTime()!= null) {
        e.setSeason(currentSeason);
        events.save(e);
      }
    }

    redirect("Programm gespeichert.",
            String.format("Das Programm für %s (%s) wurde gespeichert.",
                    currentVenue.getName(), currentVenue.getCity()));
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the currentSeason
   */
  public Season getCurrentSeason() {
    return currentSeason;
  }

  /**
   * @param currentSeason the currentSeason to set
   */
  public void setCurrentSeason(Season currentSeason) {
    this.currentSeason = currentSeason;
  }

  /**
   * @return the currentVenue
   */
  public Venue getCurrentVenue() {
    return currentVenue;
  }

  /**
   * @param currentVenue the currentVenue to set
   */
  public void setCurrentVenue(Venue currentVenue) {
    this.currentVenue = currentVenue;
  }

  /**
   * @return the venueList
   */
  public List<Venue> getVenueList() {
    return venueList;
  }

  /**
   * @param venueList the venueList to set
   */
  public void setVenueList(List<Venue> venueList) {
    this.venueList = venueList;
  }

  /**
   * @return the eventList
   */
  public List<Event> getEventList() {
    return eventList;
  }

  /**
   * @param eventList the eventList to set
   */
  public void setEventList(List<Event> eventList) {
    this.eventList = eventList;
  }

  /**
   * @return the selectedEvents
   */
  public List<Event> getSelectedEvents() {
    return selectedEvents;
  }

  /**
   * @param selectedEvents the selectedEvents to set
   */
  public void setSelectedEvents(List<Event> selectedEvents) {
    this.selectedEvents = selectedEvents;
  }

  /**
   * @return the productionVersionList
   */
  public List<ProductionVersion> getProductionVersionList() {
    return productionVersionList;
  }

  /**
   * @param productionVersionList the productionVersionList to set
   */
  public void setProductionVersionList(List<ProductionVersion> productionVersionList) {
    this.productionVersionList = productionVersionList;
  }

  /**
   * @return the hallList
   */
  public List<Hall> getHallList() {
    return hallList;
  }

  /**
   * @param hallList the hallList to set
   */
  public void setHallList(List<Hall> hallList) {
    this.hallList = hallList;
  }

  /**
   * @return the keepDate
   */
  public boolean isKeepDate() {
    return keepDate;
  }

  /**
   * @param keepDate the keepDate to set
   */
  public void setKeepDate(boolean keepDate) {
    this.keepDate = keepDate;
  }
  //</editor-fold>

}
