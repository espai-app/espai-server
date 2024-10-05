package app.espai.views.events;

import app.espai.dao.Events;
import app.espai.dao.Halls;
import app.espai.dao.ProductionVersions;
import app.espai.dao.Productions;
import app.espai.dao.ReservationSummaries;
import app.espai.dao.Venues;
import app.espai.filter.EventFilter;
import app.espai.filter.HallFilter;
import app.espai.filter.ProductionVersionFilter;
import app.espai.model.Event;
import app.espai.model.Hall;
import app.espai.model.Production;
import app.espai.model.Season;
import app.espai.model.Venue;
import app.espai.views.Dialog;
import app.espai.views.SeasonContext;
import app.espai.views.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import rocks.xprs.runtime.db.Range;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class EventIndexView {

  public enum VisibilityFilter {ALL, ONLY_VISIBLE, ONLY_HIDDEN}

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private UserContext userContext;

  @EJB
  private Events events;

  @EJB
  private Venues venues;

  @EJB
  private Halls halls;

  @EJB
  private Productions productions;

  @EJB
  private ProductionVersions productionVersions;

  @EJB
  private ReservationSummaries reservationSummaries;

  private Season currentSeason;

  private LocalDate startDate;
  private LocalDate endDate;
  private List<Production> selectedProductions;
  private List<Long> selectedProductionIds;
  private List<Venue> selectedVenues;
  private List<Long> selectedVenueIds;
  private VisibilityFilter visibility;
  private boolean filtered = false;

  private LocalDate prevWeek;
  private LocalDate nextWeek;

  private List<Event> eventList;
  private List<Production> productionList;
  private List<Venue> venueList;
  private Map<Long, Long> ticketsSold = new HashMap<>();

  @PostConstruct
  public void init() {

    currentSeason = seasonContext.getCurrentSeason();

    initFilterLists();
    EventFilter eventFilter = getFilter();

    // startDate = eventFilter.getDate().getStart();
    // endDate = eventFilter.getDate().getEnd();
    if (selectedProductions != null && !selectedProductions.isEmpty()) {
      selectedProductionIds = selectedProductions.stream()
              .map(p -> p.getId())
              .collect(Collectors.toList());
    }

    if (selectedVenues != null && !selectedVenues.isEmpty()) {
      selectedVenueIds = selectedVenues.stream()
              .map(p -> p.getId())
              .collect(Collectors.toList());
    }

    // prevWeek = startDate.minus(1, ChronoUnit.WEEKS);
    // nextWeek = endDate.plus(1, ChronoUnit.DAYS);

    // filter by halls
    eventList = new LinkedList<>(events.list(eventFilter).getItems());

    eventFilter.setParentEventIsNull(false);
    List<Event> childResult = events.list(eventFilter).getItems();
    childResult.forEach(r -> {
      if (!eventList.contains(r.getParentEvent())) {
        eventList.add(r.getParentEvent());
      }
    });
    eventList.sort(Events.DEFAULT_ORDER);

    reservationSummaries.getTicketSales(eventList).forEach(
            t -> ticketsSold.put(t.getEvent().getId(), t.getTicketsSold()));
  }

  private void initFilterLists() {

    EventFilter allEventsFilter;

    if (userContext.isRestricted()) {
      allEventsFilter = userContext.getEventFilter();
    } else {
      allEventsFilter = new EventFilter();
    }
    allEventsFilter.setSeason(currentSeason);

    List<Event> allEvents = events.list(allEventsFilter).getItems();

    productionList = allEvents
            .stream()
            .map(e -> e.getProduction() != null ? e.getProduction().getProduction() : null)
            .distinct()
            .sorted(Productions.DEFAULT_ORDER)
            .collect(Collectors.toList());

    venueList = allEvents
            .stream()
            .map(e -> e.getHall().getVenue())
            .distinct()
            .sorted(Venues.DEFAULT_ORDER)
            .collect(Collectors.toList());
  }

  private EventFilter getFilter() {

    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    EventFilter eventFilter;
    if (userContext.isRestricted()) {
      eventFilter = userContext.getEventFilter();
    } else {
      eventFilter = new EventFilter();
    }
    eventFilter.setSeason(currentSeason);

    // filter by week
//    Range<LocalDate> timeframe;
//    LocalDate now = LocalDate.now();
//
//    String startDateParam = econtext.getRequestParameterMap().get("startDate");
//    if (startDateParam != null) {
//      timeframe = calculateWeekForDate(LocalDate.parse(startDateParam));
//    } else if (currentSeason != null && currentSeason.getStart() != null && currentSeason.getStart().isAfter(now)) {
//      timeframe = calculateWeekForDate(currentSeason.getStart());
//    } else {
//      timeframe = calculateWeekForDate(now);
//    }
//    eventFilter.setDate(timeframe);

    String venueParameterName = "venue";
    String productionParameterName = "production";
    for (String k : econtext.getRequestParameterMap().keySet()) {
      if (k.endsWith(":venue")) {
        venueParameterName = k;
      }

      if (k.endsWith(":production")) {
        productionParameterName = k;
      }

      if (k.endsWith(":visibility")) {
        visibility = VisibilityFilter.valueOf(econtext.getRequestParameterMap().get(k));
      }
    }

    // filter by selected venues
    if (!userContext.isRestricted()) {
      selectedVenues = new LinkedList<>();
      String[] venueParams = econtext.getRequestParameterValuesMap().get(venueParameterName);
      if (venueParams != null) {
        for (String id : venueParams) {
          try {
            Venue v = venues.get(Long.parseLong(id));
            if (v != null) {
              selectedVenues.add(v);
            }
          } catch (NumberFormatException ex) {
            // ignore
          }
        }

        HallFilter hallFilter = new HallFilter();
        hallFilter.setVenues(selectedVenues);
        List<Hall> hallList = halls.list(hallFilter).getItems();
        eventFilter.setHalls(hallList);

        filtered |= !selectedVenues.isEmpty();
      }

      // filter by production
      String[] productionParams = econtext.getRequestParameterValuesMap().get(productionParameterName);
      if (productionParams != null) {
        selectedProductions = new LinkedList<>();
        for (String id : productionParams) {
          try {
            Production p = productions.get(Long.parseLong(id));
            if (p != null) {
              selectedProductions.add(p);
            }

            ProductionVersionFilter versionFilter = new ProductionVersionFilter();
            versionFilter.setProductions(selectedProductions);

            eventFilter.setProductions(productionVersions.list(versionFilter).getItems());
          } catch (NumberFormatException ex) {
            // ignore
          }
        }

        filtered |= !selectedProductions.isEmpty();
      }

      if (visibility != null && visibility != VisibilityFilter.ALL) {
        eventFilter.setHidden(visibility == VisibilityFilter.ONLY_HIDDEN);
        filtered = true;
      }
    }

    return eventFilter;
  }

  public void createEvent() {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("seasonId", Arrays.asList(String.valueOf(currentSeason.getId())));
    PrimeFaces.current().dialog().openDynamic("editor", Dialog.getDefaultOptions(800, 780), params);
  }

  private Range<LocalDate> calculateWeekForDate(LocalDate currentDate) {
    LocalDate startRange = currentDate;

    int dayOfWeek = currentDate.getDayOfWeek().getValue();
    if (dayOfWeek != 1) {
      startRange = currentDate.minus(dayOfWeek - 1, ChronoUnit.DAYS);
    }

    LocalDate endRange = startRange.plus(6, ChronoUnit.DAYS);

    return new Range<>(startRange, endRange);
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
   * @return the startDate
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * @param startDate the startDate to set
   */
  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  /**
   * @return the endDate
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * @param endDate the endDate to set
   */
  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  /**
   * @return the selectedProductions
   */
  public List<Production> getSelectedProductions() {
    return selectedProductions;
  }

  /**
   * @param selectedProductions the selectedProductions to set
   */
  public void setSelectedProductions(List<Production> selectedProductions) {
    this.selectedProductions = selectedProductions;
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

  /**
   * @return the selectedProductionIds
   */
  public List<Long> getSelectedProductionIds() {
    return selectedProductionIds;
  }

  /**
   * @param selectedProductionIds the selectedProductionIds to set
   */
  public void setSelectedProductionIds(List<Long> selectedProductionIds) {
    this.selectedProductionIds = selectedProductionIds;
  }

  /**
   * @return the selectedVenueIds
   */
  public List<Long> getSelectedVenueIds() {
    return selectedVenueIds;
  }

  /**
   * @param selectedVenueIds the selectedVenueIds to set
   */
  public void setSelectedVenueIds(List<Long> selectedVenueIds) {
    this.selectedVenueIds = selectedVenueIds;
  }

  /**
   * @return the visibility
   */
  public VisibilityFilter getVisibility() {
    return visibility;
  }

  /**
   * @param visibility the visibility to set
   */
  public void setVisibility(VisibilityFilter visibility) {
    this.visibility = visibility;
  }

  /**
   * @return the filtered
   */
  public boolean isFiltered() {
    return filtered;
  }

  /**
   * @param filtered the filtered to set
   */
  public void setFiltered(boolean filtered) {
    this.filtered = filtered;
  }

  /**
   * @return the prevWeek
   */
  public LocalDate getPrevWeek() {
    return prevWeek;
  }

  /**
   * @param prevWeek the prevWeek to set
   */
  public void setPrevWeek(LocalDate prevWeek) {
    this.prevWeek = prevWeek;
  }

  /**
   * @return the nextWeek
   */
  public LocalDate getNextWeek() {
    return nextWeek;
  }

  /**
   * @param nextWeek the nextWeek to set
   */
  public void setNextWeek(LocalDate nextWeek) {
    this.nextWeek = nextWeek;
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
   * @return the productionList
   */
  public List<Production> getProductionList() {
    return productionList;
  }

  /**
   * @param productionList the productionList to set
   */
  public void setProductionList(List<Production> productionList) {
    this.productionList = productionList;
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
   * @return the ticketsSold
   */
  public Map<Long, Long> getTicketsSold() {
    return ticketsSold;
  }

  /**
   * @param ticketsSold the ticketsSold to set
   */
  public void setTicketsSold(Map<Long, Long> ticketsSold) {
    this.ticketsSold = ticketsSold;
  }
  //</editor-fold>

}
