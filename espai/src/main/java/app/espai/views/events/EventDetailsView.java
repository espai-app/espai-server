package app.espai.views.events;

import app.espai.businesslogic.CapacityCalculator;
import app.espai.businesslogic.PriceManager;
import app.espai.dao.EventTicketPrices;
import app.espai.dao.Events;
import app.espai.dao.ReservationSummaries;
import app.espai.filter.EventFilter;
import app.espai.filter.EventTicketPriceFilter;
import app.espai.filter.ReservationSummaryFilter;
import app.espai.model.Event;
import app.espai.model.EventTicketPrice;
import app.espai.model.ReservationSummary;
import app.espai.model.SeatCategory;
import app.espai.views.BaseView;
import app.espai.views.Dialog;
import app.espai.views.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import rocks.xprs.runtime.exceptions.AccessDeniedException;
import rocks.xprs.runtime.exceptions.InvalidDataException;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class EventDetailsView extends BaseView {

  @EJB
  private UserContext userContext;

  @EJB
  private Events events;

  @EJB
  private ReservationSummaries reservations;

  @EJB
  private EventTicketPrices ticketPrices;

  @EJB
  private PriceManager priceManager;
  
  @Inject
  private CapacityCalculator capacityCalculator;

  private Event event;
  private List<Event> childEventList;
  private List<ReservationSummary> reservationList;
  private List<EventTicketPrice> ticketPriceList;
  private List<SeatCategory> seats;
  private int totalTickets;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String eventIdParam = econtext.getRequestParameterMap().get("eventId");
    if (eventIdParam == null || !eventIdParam.matches("\\d+")) {
      throw new InvalidDataException("eventId missing.");
    }

    event = events.get(Long.parseLong(eventIdParam));

    if (userContext.isRestricted() && event.getHall() != null && event.getHall().getVenue() != null
            && !userContext.getVenues().contains(event.getHall().getVenue())) {
      throw new AccessDeniedException("Zugriff verweigert.");
    }

    EventFilter eventFilter = new EventFilter();
    eventFilter.setParentEvent(event);

    childEventList = events.list(eventFilter).getItems();

    ReservationSummaryFilter reservationFilter = new ReservationSummaryFilter();
    reservationFilter.setEvent(event);
    reservationList = reservations.list(reservationFilter).getItems();

    EventTicketPriceFilter ticketFilter = new EventTicketPriceFilter();
    ticketFilter.setEvent(event);
    ticketPriceList = ticketPrices.list(ticketFilter).getItems();
    
    seats = capacityCalculator.getSeats(event);
    totalTickets = reservationList.stream()
            .collect(Collectors.summingInt(ReservationSummary::getTickets));
  }

  public void editEvent() {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("seasonId", Arrays.asList(String.valueOf(event.getSeason().getId())));
    params.put("eventId", Arrays.asList(String.valueOf(event.getId())));
    PrimeFaces.current().dialog().openDynamic("editor", Dialog.getDefaultOptions(800, 780), params);
  }

  public void deleteEvent() {
    events.delete(event);
    redirect("index.xhtml?seasonId=" + event.getSeason().getId(),
            "Veranstaltung gelöscht",
            null,
            FacesMessage.SEVERITY_INFO);
  }

  public void addChildEvent() {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("seasonId", Arrays.asList(String.valueOf(event.getSeason().getId())));
    params.put("parentEventId", Arrays.asList(String.valueOf(event.getId())));
    PrimeFaces.current().dialog().openDynamic("editor", Dialog.getDefaultOptions(800, 600), params);
  }

  public void editChildEvent(String eventId) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("seasonId", Arrays.asList(String.valueOf(event.getSeason().getId())));
    params.put("eventId", Arrays.asList(eventId));
    PrimeFaces.current().dialog().openDynamic("editor", Dialog.getDefaultOptions(800, 600), params);
  }

  public void removeChildEvent(Long eventId) {
    Event childEvent = events.get(eventId);
    events.delete(childEvent);
    redirect("Unterveranstaltung gelöscht", null);
  }

  public void addTicketPrice() {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("eventId", Arrays.asList(String.valueOf(event.getId())));
    PrimeFaces.current().dialog().openDynamic(
            "ticketPriceEditor",
            Dialog.getDefaultOptions(500, 400), params);
  }

  public void editTicketPrice(Long id) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("ticketPriceId", Arrays.asList(String.valueOf(id)));
    PrimeFaces.current().dialog().openDynamic(
            "ticketPriceEditor",
            Dialog.getDefaultOptions(500, 400), params);
  }

  public void removeTicketPrice(Long id) {
    EventTicketPrice ticketPrice = ticketPrices.get(id);
    ticketPrices.delete(ticketPrice);
    init();
  }

  public void addDefaultTicketPrices() {
    if (ticketPriceList.isEmpty()) {
      ticketPriceList = priceManager.getPrices(event);
      for (EventTicketPrice p : ticketPriceList) {
        ticketPrices.save(p);
      }
    }
  }

  public void onEventChanged(SelectEvent<Event> selectEvent) {
    init();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the event
   */
  public Event getEvent() {
    return event;
  }

  /**
   * @param event the event to set
   */
  public void setEvent(Event event) {
    this.event = event;
  }

  /**
   * @return the childEventList
   */
  public List<Event> getChildEventList() {
    return childEventList;
  }

  /**
   * @param childEventList the childEventList to set
   */
  public void setChildEventList(List<Event> childEventList) {
    this.childEventList = childEventList;
  }

  /**
   * @return the reservationList
   */
  public List<ReservationSummary> getReservationList() {
    return reservationList;
  }

  /**
   * @param reservationList the reservationList to set
   */
  public void setReservationList(List<ReservationSummary> reservationList) {
    this.reservationList = reservationList;
  }

  /**
   * @return the ticketPriceList
   */
  public List<EventTicketPrice> getTicketPriceList() {
    return ticketPriceList;
  }

  /**
   * @param ticketPriceList the ticketPriceList to set
   */
  public void setTicketPriceList(List<EventTicketPrice> ticketPriceList) {
    this.ticketPriceList = ticketPriceList;
  }

  /**
   * @return the seats
   */
  public List<SeatCategory> getSeats() {
    return seats;
  }

  /**
   * @param seats the seats to set
   */
  public void setSeats(List<SeatCategory> seats) {
    this.seats = seats;
  }

  /**
   * @return the totalTickets
   */
  public int getTotalTickets() {
    return totalTickets;
  }

  /**
   * @param totalTickets the totalTickets to set
   */
  public void setTotalTickets(int totalTickets) {
    this.totalTickets = totalTickets;
  }
  //</editor-fold>
}
