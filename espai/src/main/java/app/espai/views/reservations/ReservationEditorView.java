package app.espai.views.reservations;

import app.espai.dao.Events;
import app.espai.dao.HallCapacities;
import app.espai.dao.Halls;
import app.espai.dao.PriceCategories;
import app.espai.dao.ReservationExtras;
import app.espai.dao.ReservationSummaries;
import app.espai.dao.ReservationTickets;
import app.espai.dao.Reservations;
import app.espai.dao.SeasonVenues;
import app.espai.filter.EventFilter;
import app.espai.filter.HallCapacityFilter;
import app.espai.filter.HallFilter;
import app.espai.filter.ReservationExtraFilter;
import app.espai.filter.ReservationFilter;
import app.espai.filter.ReservationTicketFilter;
import app.espai.model.Event;
import app.espai.model.Hall;
import app.espai.model.HallCapacity;
import app.espai.model.PriceCategory;
import app.espai.model.Reservation;
import app.espai.model.ReservationExtra;
import app.espai.model.ReservationStatus;
import app.espai.model.ReservationTicket;
import app.espai.model.SeatCategory;
import app.espai.model.TicketStatistic;
import app.espai.model.Venue;
import app.espai.views.BaseView;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class ReservationEditorView extends BaseView implements Serializable {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private Reservations reservations;

  @EJB
  private ReservationTickets reservationTickets;

  @EJB
  private ReservationExtras reservationExtras;

  @EJB
  private ReservationSummaries reservationSummaries;

  @EJB
  private SeasonVenues seasonVenues;

  @EJB
  private Halls halls;

  @EJB
  private HallCapacities hallCapacities;

  @EJB
  private PriceCategories priceCategories;

  @EJB
  private Events events;

  private Reservation reservation;
  private List<ReservationTicket> ticketList;
  private List<Reservation> childReservationList;
  private List<ReservationExtra> extraList;

  private List<Venue> venueList;
  private List<Event> eventList;
  private List<Event> childEventList;
  private List<? extends PriceCategory> priceCategoryList;
  private List<SeatCategory> seatCategoryList;

  private Map<Long, Integer> seatsAvailable = new HashMap<>();
  private Venue selectedVenue;
  private List<Event> selectedChildEventList;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String reservationIdParam = econtext.getRequestParameterMap().get("reservationId");

    // selection lists
    venueList = seasonVenues.getVenues(seasonContext.getCurrentSeason());
    priceCategoryList = priceCategories.list().getItems();

    // open or create reservation?
    if (reservationIdParam == null || !reservationIdParam.matches("\\d+")) {
      reservation = new Reservation();
      reservation.setStatus(ReservationStatus.NEW);
      
      ticketList = new LinkedList<>();
      childReservationList = new LinkedList<>();
      selectedChildEventList = new LinkedList<>();
      extraList = new LinkedList<>();

      selectedVenue = venueList.get(0);
    } else {
      reservation = reservations.get(Long.parseLong(reservationIdParam));

      ReservationTicketFilter ticketFilter = new ReservationTicketFilter();
      ticketFilter.setReservation(reservation);
      ticketList = reservationTickets.list(ticketFilter).getItems();

      ReservationFilter childFilter = new ReservationFilter();
      childFilter.setParentReservation(reservation);
      childReservationList = new LinkedList<>(reservations.list(childFilter).getItems());

      selectedChildEventList = childReservationList.stream().map(Reservation::getEvent).toList();

      ReservationExtraFilter extraFilter = new ReservationExtraFilter();
      extraFilter.setReservation(reservation);
      extraList = reservationExtras.list(extraFilter).getItems();

      selectedVenue = (reservation.getEvent() != null && reservation.getEvent().getHall() != null)
              ? reservation.getEvent().getHall().getVenue()
              : null;
    }

    onVenueChanged();
    onEventChanged();
  }

  public String formatDate(LocalDate date, LocalTime time) {
    LocalDateTime dateTime = LocalDateTime.of(date, time);
    return dateTime.format(DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT));
  }

  public void save() {

    // save main reservation
    reservation = reservations.save(reservation);

    for (ReservationTicket ticket : ticketList) {
      ticket.setReservation(reservation);
      reservationTickets.save(ticket);
    }

    for (ReservationExtra extra : extraList) {
      extra.setReservation(reservation);
      reservationExtras.save(extra);
    }

    // equalize list of child reservation with selected events
    if (selectedChildEventList.size() > childReservationList.size()) {
      for (int i = childReservationList.size() + 1; i <= selectedChildEventList.size(); i++) {
        childReservationList.add(new Reservation());
      }
    } else if (selectedChildEventList.size() < childReservationList.size()) {
      for (int i = selectedChildEventList.size(); i < childReservationList.size(); i++) {
        reservations.delete(childReservationList.get(i));
        childReservationList.remove(i);
      }
    }

    // save child reservations
    for (int i = 0; i < selectedChildEventList.size(); i++) {
      Event currentEvent = selectedChildEventList.get(i);
      Reservation childRes = childReservationList.get(i);

      List<ReservationTicket> childTicketList;
      if (childRes.getId() != null) {
        ReservationTicketFilter childTicketFilter = new ReservationTicketFilter();
        childTicketFilter.setReservation(childRes);
        childTicketList = reservationTickets.list(childTicketFilter).getItems();
      } else {
        childTicketList = new LinkedList<>();
      }

      if (childTicketList.size() < ticketList.size()) {
        for (int j = childTicketList.size() + 1; j <= ticketList.size(); j++) {
          childTicketList.add(new ReservationTicket());
        }
      } else if (childTicketList.size() > ticketList.size()) {
        for (int j = ticketList.size() + 1; j <= childTicketList.size(); j++) {
          reservationTickets.delete(childTicketList.get(j));
          childTicketList.remove(j);
        }
      }

      childRes = copyReservation(reservation, childRes);
      childRes.setEvent(currentEvent);
      reservations.save(childRes);

      for (int j = 0; j < ticketList.size(); j++) {
        ReservationTicket childTicket = copyTicket(ticketList.get(j), childTicketList.get(j));
        childTicket.setReservation(childRes);
        reservationTickets.save(childTicket);
      }
    }

    redirect("details.xhtml?reservationId=%d&seasonId=%d".formatted(
            reservation.getId(),
            seasonContext.getCurrentSeason().getId()),
            "Buchung gespeichert",
            null,
            FacesMessage.SEVERITY_INFO);
  }

  public void onVenueChanged() {
    HallFilter hallFilter = new HallFilter();
    hallFilter.setVenue(selectedVenue);
    List<Hall> hallList = halls.list(hallFilter).getItems();

    EventFilter eventFilter = new EventFilter();
    eventFilter.setHalls(hallList);
    eventFilter.setSeason(seasonContext.getCurrentSeason());
    eventList = events.list(eventFilter).getItems();
    eventList.sort(Events.DEFAULT_ORDER);

    List<TicketStatistic> ticketSales = reservationSummaries.getTicketSales(eventList);
    seatsAvailable = new HashMap<>();

    for (TicketStatistic s : ticketSales) {
      seatsAvailable.put(
              s.getEvent().getId(),
              s.getEvent().getCapacity() - s.getTicketsSold().intValue());
    }

    for (Event e : eventList) {
      if (!seatsAvailable.containsKey(e.getId())) {
        seatsAvailable.put(e.getId(), e.getCapacity());
      }
    }
  }

  public void onEventChanged() {
    if (reservation.getEvent() != null) {
      EventFilter childFilter = new EventFilter();
      childFilter.setParentEvent(reservation.getEvent());
      childEventList = events.list(childFilter).getItems();

      HallCapacityFilter hallCapacityFilter = new HallCapacityFilter();
      hallCapacityFilter.setHall(reservation.getEvent().getHall());
      seatCategoryList = hallCapacities.list(hallCapacityFilter).getItems().stream()
              .map(HallCapacity::getSeatCategory)
              .toList();
    } else {
      childEventList = new LinkedList<>();
      seatCategoryList = new LinkedList<>();
    }
  }

  public void addTickets() {
    ReservationTicket ticket = new ReservationTicket();
    ticket.setPrice(new Monetary(BigDecimal.ZERO, "EUR"));
    ticketList.add(ticket);

  }

  public void removeTickets(int index) {
    if (index >= 0 && index < ticketList.size()) {
      ReservationTicket ticket = ticketList.get(index);
      if (ticket.getId() != null) {
        reservationTickets.delete(ticket);
      }

      ticketList.remove(index);
    }
  }

  public void addExtra() {
    extraList.add(new ReservationExtra());
  }

  public void removeExtra(int index) {
    if (index >= 0 && index < extraList.size()) {
      ReservationExtra extra = extraList.get(index);
      if (extra.getId() != null) {
        reservationExtras.delete(extra);
      }
      extraList.remove(index);
    }
  }

  private Reservation copyReservation(Reservation source, Reservation target) {
    target.setParentReservation(source);
    target.setCompany(source.getCompany());
    target.setGivenName(source.getGivenName());
    target.setSurname(source.getSurname());
    target.setAddress(source.getAddress());
    target.setPostcode(source.getPostcode());
    target.setCity(source.getCity());
    target.setPhone(source.getPhone());
    target.setEmail(source.getEmail());
    target.setStatus(reservation.getStatus());

    return target;
  }

  private ReservationTicket copyTicket(ReservationTicket source, ReservationTicket target) {
    target.setAmount(source.getAmount());
    target.setPrice(source.getPrice());
    target.setSeatCategory(source.getSeatCategory());
    target.setPriceCategory(source.getPriceCategory());
    return target;
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the reservation
   */
  public Reservation getReservation() {
    return reservation;
  }

  /**
   * @param reservation the reservation to set
   */
  public void setReservation(Reservation reservation) {
    this.reservation = reservation;
  }

  /**
   * @return the ticketList
   */
  public List<ReservationTicket> getTicketList() {
    return ticketList;
  }

  /**
   * @param ticketList the ticketList to set
   */
  public void setTicketList(List<ReservationTicket> ticketList) {
    this.ticketList = ticketList;
  }

  /**
   * @return the childReservationList
   */
  public List<Reservation> getChildReservationList() {
    return childReservationList;
  }

  /**
   * @param childReservationList the childReservationList to set
   */
  public void setChildReservationList(List<Reservation> childReservationList) {
    this.childReservationList = childReservationList;
  }

  /**
   * @return the extraList
   */
  public List<ReservationExtra> getExtraList() {
    return extraList;
  }

  /**
   * @param extraList the extraList to set
   */
  public void setExtraList(List<ReservationExtra> extraList) {
    this.extraList = extraList;
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
   * @return the priceCategoryList
   */
  public List<? extends PriceCategory> getPriceCategoryList() {
    return priceCategoryList;
  }

  /**
   * @param priceCategoryList the priceCategoryList to set
   */
  public void setPriceCategoryList(List<? extends PriceCategory> priceCategoryList) {
    this.priceCategoryList = priceCategoryList;
  }

  /**
   * @return the seatCategoryList
   */
  public List<SeatCategory> getSeatCategoryList() {
    return seatCategoryList;
  }

  /**
   * @param seatCategoryList the seatCategoryList to set
   */
  public void setSeatCategoryList(List<SeatCategory> seatCategoryList) {
    this.seatCategoryList = seatCategoryList;
  }

  /**
   * @return the seatsAvailable
   */
  public Map<Long, Integer> getSeatsAvailable() {
    return seatsAvailable;
  }

  /**
   * @param seatsAvailable the seatsAvailable to set
   */
  public void setSeatsAvailable(Map<Long, Integer> seatsAvailable) {
    this.seatsAvailable = seatsAvailable;
  }

  /**
   * @return the selectedVenue
   */
  public Venue getSelectedVenue() {
    return selectedVenue;
  }

  /**
   * @param selectedVenue the selectedVenue to set
   */
  public void setSelectedVenue(Venue selectedVenue) {
    this.selectedVenue = selectedVenue;
  }

  /**
   * @return the selectedChildEventList
   */
  public List<Event> getSelectedChildEventList() {
    return selectedChildEventList;
  }

  /**
   * @param selectedChildEventList the selectedChildEventList to set
   */
  public void setSelectedChildEventList(List<Event> selectedChildEventList) {
    this.selectedChildEventList = selectedChildEventList;
  }
  //</editor-fold>

}
