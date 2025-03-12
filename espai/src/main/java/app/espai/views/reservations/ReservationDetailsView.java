package app.espai.views.reservations;

import app.espai.ReservationMailManager;
import app.espai.businesslogic.CapacityCalculator;
import app.espai.dao.ReservationExtras;
import app.espai.dao.ReservationTickets;
import app.espai.dao.Reservations;
import app.espai.events.ReservationChangedEvent;
import app.espai.filter.ReservationExtraFilter;
import app.espai.filter.ReservationFilter;
import app.espai.filter.ReservationTicketFilter;
import app.espai.model.Production;
import app.espai.model.Reservation;
import app.espai.model.ReservationExtra;
import app.espai.model.ReservationStatus;
import app.espai.model.ReservationTicket;
import app.espai.model.SeatCategory;
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
import java.util.List;
import java.util.Map;
import org.primefaces.PrimeFaces;
import rocks.xprs.mail.Mail;
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
  
  @Inject
  private CapacityCalculator capacityCalculator;

  @Inject
  private jakarta.enterprise.event.Event<ReservationChangedEvent> reservationChangedEvent;

  private Reservation reservation;
  private ReservationStatus reservationStatus;
  private Production production;
  private List<ReservationTicket> ticketList;
  private List<Reservation> childReservationList;
  private List<ReservationExtra> extraList;
  private List<SeatCategory> seats;

  private final ReservationStatus[] reservationStatusList = ReservationStatus.values();

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
    
    seats = capacityCalculator.getSeats(reservation.getEvent());
  }

  public void updateStatus() {

    Mail mail = switch (reservationStatus) {
      case NEW -> mailManager.createNew(reservation);
      case HOLD -> mailManager.createHold(reservation);
      case CONFIRMED -> mailManager.createConfirmed(reservation);
      case CANCELED -> mailManager.createCanceled(reservation);
      default -> null;
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

  public void delete() {
    reservations.delete(reservation);
    redirect("index.xhtml?seasonId=" + String.valueOf(reservation.getEvent().getSeason().getId()),
            "Bestellung gelöscht",
            null,
            FacesMessage.SEVERITY_INFO);
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
   * @return the reservationStatusList
   */
  public ReservationStatus[] getReservationStatusList() {
    return reservationStatusList;
  }
  //</editor-fold>
}
