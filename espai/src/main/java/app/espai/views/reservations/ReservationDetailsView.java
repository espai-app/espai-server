package app.espai.views.reservations;

import app.espai.ReservationMailManager;
import app.espai.dao.EventTicketPrices;
import app.espai.dao.HallCapacities;
import app.espai.dao.ReservationExtras;
import app.espai.dao.ReservationTickets;
import app.espai.dao.Reservations;
import app.espai.dao.TicketSummaries;
import app.espai.events.ReservationChangedEvent;
import app.espai.filter.EventTicketPriceFilter;
import app.espai.filter.HallCapacityFilter;
import app.espai.filter.ReservationExtraFilter;
import app.espai.filter.ReservationFilter;
import app.espai.filter.ReservationTicketFilter;
import app.espai.filter.TicketSummaryFilter;
import app.espai.model.EventTicketPrice;
import app.espai.model.HallCapacity;
import app.espai.model.PriceCategory;
import app.espai.model.Production;
import app.espai.model.Reservation;
import app.espai.model.ReservationExtra;
import app.espai.model.ReservationStatus;
import app.espai.model.ReservationTicket;
import app.espai.model.SeatCategory;
import app.espai.model.TicketSummary;
import app.espai.views.BaseView;
import app.espai.views.Dialog;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import rocks.xprs.mail.Mail;
import rocks.xprs.runtime.db.Range;
import rocks.xprs.runtime.exceptions.InvalidDataException;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class ReservationDetailsView extends BaseView {

  @EJB
  private Reservations reservations;

  @EJB
  private ReservationTickets reservationTickets;

  @EJB
  private ReservationExtras reservationExtras;

  @EJB
  private ReservationMailManager mailManager;

  @EJB
  private EventTicketPrices prices;

  @EJB
  private HallCapacities hallCapacities;

  @Inject
  private jakarta.enterprise.event.Event<ReservationChangedEvent> reservationChangedEvent;

  @EJB
  private TicketSummaries ticketSummaries;

  private Reservation reservation;
  private ReservationStatus reservationStatus;
  private Production production;
  private List<ReservationTicket> ticketList;
  private List<Reservation> childReservationList;
  private List<ReservationExtra> extraList;

  private Map<String, Map<String, Map<String, Integer>>> ticketTable;
  private List<String> priceCategoryNames;
  private List<String> seatCategoryNames;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String reservationIdParam = econtext.getRequestParameterMap().get("reservationId");

    if (reservationIdParam == null || !reservationIdParam.matches("\\d+")) {
      throw new InvalidDataException("reservationId missing");
    }

    reservation = reservations.get(Long.parseLong(reservationIdParam));
    reservationStatus = reservation.getStatus();
    production = reservation.getEvent().getProduction().getProduction();

    ReservationTicketFilter ticketFilter = new ReservationTicketFilter();
    ticketFilter.setReservation(reservation);
    ticketList = reservationTickets.list(ticketFilter).getItems();

    ReservationFilter childFilter = new ReservationFilter();
    childFilter.setParentReservation(reservation);
    childReservationList = reservations.list(childFilter).getItems();

    ReservationExtraFilter extraFilter = new ReservationExtraFilter();
    extraFilter.setReservation(reservation);
    extraList = reservationExtras.list(extraFilter).getItems();

    aggregateTickets();
  }

  public void updateStatus() {

    Mail mail = switch (reservationStatus) {
      case NEW ->
        mailManager.createNew(reservation);
      case HOLD ->
        mailManager.createHold(reservation);
      case CONFIRMED ->
        mailManager.createConfirmed(reservation);
      case CANCELED ->
        mailManager.createCanceled(reservation);
      default ->
        null;
    };

    ReservationStatus oldStatus = reservation.getStatus();
    ReservationStatus newStatus = reservationStatus;

    reservation.setStatus(reservationStatus);
    reservations.save(reservation);

    reservationChangedEvent.fireAsync(new ReservationChangedEvent(reservation, oldStatus, newStatus));

    if (mail != null) {
      FacesContext.getCurrentInstance().getExternalContext().getFlash().put("mail", mail);
      Map<String, List<String>> params = new HashMap<>();

      params.put("seasonId", List.of(String.valueOf(reservation.getEvent().getSeason().getId())));

      PrimeFaces.current().dialog().openDynamic(
              "/mail/editor",
              Dialog.getDefaultOptions(800, 800),
              params);
    }
  }

  public void sendAnswerMessage() {

    Mail mail = mailManager.createMessage(reservation);

    if (mail != null) {
      FacesContext.getCurrentInstance().getExternalContext().getFlash().put("mail", mail);
      Map<String, List<String>> params = new HashMap<>();

      params.put("seasonId", List.of(String.valueOf(reservation.getEvent().getSeason().getId())));

      PrimeFaces.current().dialog().openDynamic(
              "/mail/editor",
              Dialog.getDefaultOptions(800, 800),
              params);
    }
  }

  public void delete() {
    reservations.delete(reservation);
    redirect("index.xhtml?seasonId=" + String.valueOf(reservation.getEvent().getSeason().getId()),
            "Bestellung gelöscht",
            null,
            FacesMessage.SEVERITY_INFO);
  }

  private void aggregateTickets() {

    // get ticket prices
    EventTicketPriceFilter ticketPriceFilter = new EventTicketPriceFilter();
    ticketPriceFilter.setEvent(reservation.getEvent());

    List<EventTicketPrice> priceList = prices.list(ticketPriceFilter).getItems();

    // get seat categories
    HallCapacityFilter hallCapacityFilter = new HallCapacityFilter();
    hallCapacityFilter.setHall(reservation.getEvent().getHall());

    Map<Long, List<HallCapacity>> capacityMap = hallCapacities
            .list(hallCapacityFilter)
            .getItems()
            .stream()
            .collect(Collectors.groupingBy(c -> c.getSeatCategory().getId()));

    // get totals
    TicketSummaryFilter ticketFilter = new TicketSummaryFilter();
    ticketFilter.setDate(new Range<>(reservation.getEvent().getDate(), reservation.getEvent().getDate()));
    ticketFilter.setVenue(reservation.getEvent().getHall().getVenue());

    List<TicketSummary> ticketTotals = ticketSummaries.list(ticketFilter).getItems();

    List<SeatCategory> seatCategoryList = priceList.stream()
            .map(t -> {
              if (capacityMap.containsKey(t.getSeatCategory().getId())) {
                t.getSeatCategory().setCapacity(
                        capacityMap.get(t.getSeatCategory().getId()).get(0).getCapacity());
              } else {
                t.getSeatCategory().setCapacity(0);
              }

              return t.getSeatCategory();
            })
            .distinct()
            .toList();

    seatCategoryNames = seatCategoryList.stream()
            .map(SeatCategory::getName)
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER::compare)
            .toList();

    priceCategoryNames = priceList.stream()
            .map(EventTicketPrice::getPriceCategory)
            .map(PriceCategory::getName)
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER::compare)
            .toList();

    // prepare target table
    // name -> priceCategory -> seatCategory -> value
    ticketTable = new LinkedHashMap<>();

    // init free seats with capacity
    Map<String, Integer> freeSeatsSeatCategoryDimension = new LinkedHashMap<>();
    for (SeatCategory seatCategory : seatCategoryList) {
      freeSeatsSeatCategoryDimension.put(seatCategory.getName(), seatCategory.getCapacity());
    }

    Map<String, Map<String, Integer>> freeSeatsPriceCategoryDimension = new LinkedHashMap<>();
    freeSeatsPriceCategoryDimension.put("", freeSeatsSeatCategoryDimension);

    ticketTable.put(TicketSummary.SEATS_FREE_EVENT, freeSeatsPriceCategoryDimension);

    // init seats taken (by event and by day)
    List<String> names = List.of(TicketSummary.SEATS_TAKEN_EVENT, TicketSummary.SEATS_TAKEN_DAY);

    for (String name : names) {
      Map<String, Map<String, Integer>> priceDimension = new LinkedHashMap<>();
      for (String priceCategory : priceCategoryNames) {
        Map<String, Integer> seatDimension = new LinkedHashMap<>();
        for (String seatCategory : seatCategoryNames) {
          seatDimension.put(seatCategory, 0);
        }
        priceDimension.put(priceCategory, seatDimension);
      }
      ticketTable.put(name, priceDimension);
    }

    // fill in values
    for (TicketSummary t : ticketTotals) {

      if (!freeSeatsSeatCategoryDimension.containsKey(t.getSeatCategory().getName())) {
        continue;
      }

      if (t.getEvent().equals(reservation.getEvent())) {

        // update free seats
        freeSeatsSeatCategoryDimension.put(
                t.getSeatCategory().getName(),
                freeSeatsSeatCategoryDimension.get(t.getSeatCategory().getName()) - t.getTickets());

        // seats taken (this event)
        ticketTable.get(TicketSummary.SEATS_TAKEN_EVENT)
                .get(t.getPriceCategory().getName())
                .put(t.getSeatCategory().getName(), t.getTickets());
      }

      // seats taken (this venue and day)
      if (t.getEvent().getParentEvent() == null) {
        Map<String, Integer> dayTotalSeatsDimension = ticketTable
                .get(TicketSummary.SEATS_TAKEN_DAY)
                .get(t.getPriceCategory().getName());

        dayTotalSeatsDimension.put(
                t.getSeatCategory().getName(),
                dayTotalSeatsDimension.get(t.getSeatCategory().getName()) + t.getTickets());
      }
    }
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
   * @return the reservationStatus
   */
  public ReservationStatus getReservationStatus() {
    return reservationStatus;
  }

  /**
   * @param reservationStatus the reservationStatus to set
   */
  public void setReservationStatus(ReservationStatus reservationStatus) {
    this.reservationStatus = reservationStatus;
  }

  /**
   * @return the production
   */
  public Production getProduction() {
    return production;
  }

  /**
   * @param production the production to set
   */
  public void setProduction(Production production) {
    this.production = production;
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
   * @return the ticketTable
   */
  public Map<String, Map<String, Map<String, Integer>>> getTicketTable() {
    return ticketTable;
  }

  /**
   * @param ticketTable the ticketTable to set
   */
  public void setTicketTable(Map<String, Map<String, Map<String, Integer>>> ticketTable) {
    this.ticketTable = ticketTable;
  }

  /**
   * @return the priceCategoryNames
   */
  public List<String> getPriceCategoryNames() {
    return priceCategoryNames;
  }

  /**
   * @param priceCategoryNames the priceCategoryNames to set
   */
  public void setPriceCategoryNames(List<String> priceCategoryNames) {
    this.priceCategoryNames = priceCategoryNames;
  }

  /**
   * @return the seatCategoryNames
   */
  public List<String> getSeatCategoryNames() {
    return seatCategoryNames;
  }

  /**
   * @param seatCategoryNames the seatCategoryNames to set
   */
  public void setSeatCategoryNames(List<String> seatCategoryNames) {
    this.seatCategoryNames = seatCategoryNames;
  }
  //</editor-fold>

}
