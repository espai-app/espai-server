package app.espai.webservice;

import app.espai.dao.EventTicketPrices;
import app.espai.dao.Events;
import app.espai.dao.ReservationExtras;
import app.espai.dao.ReservationSummaries;
import app.espai.dao.ReservationTickets;
import app.espai.dao.Reservations;
import app.espai.events.ReservationChangedEvent;
import app.espai.model.Event;
import app.espai.model.EventTicketPrice;
import app.espai.model.Reservation;
import app.espai.model.ReservationExtra;
import app.espai.model.ReservationStatus;
import app.espai.model.ReservationTicket;
import app.espai.model.TicketStatistic;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 *
 * @author rborowski
 */
@Stateless
@Path("reservations")
public class ReservationWebservice {

  @EJB
  private Events events;

  @EJB
  private Reservations reservations;

  @EJB
  private ReservationExtras extras;

  @EJB
  private ReservationTickets reservationTickets;

  @EJB
  private EventTicketPrices ticketPrices;

  @EJB
  private ReservationSummaries reservationSummaries;

  @Inject
  private jakarta.enterprise.event.Event<ReservationChangedEvent> reservationChangedEvent;

  @POST
  @Path("validate")
  public List<String> validate(ReservationDTO reservation) {

    List<String> errors = new LinkedList<>();

    try {
      List<Event> capacityCheck = new LinkedList<>();

      // check if event exists
      Event event = events.get(reservation.getEvent());
      capacityCheck.add(event);

      if (reservation.getChildEvents() != null && !reservation.getChildEvents().isEmpty()) {
        for (Long cid : reservation.getChildEvents()) {
          capacityCheck.add(events.get(cid));
        }
      }

      // sum up tickets
      int totalTickets = 0;
      if (reservation.getTickets() != null) {
        for (Integer t : reservation.getTickets().values()) {
          totalTickets += t;
        }
      }

      if (totalTickets <= 0) {
        errors.add("Die Anzahl der Tickets muss größer 0 sein.");
      } else {

        List<TicketStatistic> soldTickets = reservationSummaries.getTicketSales(capacityCheck);
        Integer capacity = event.getTicketLimit() != null
                ? event.getTicketLimit()
                : event.getHall().getCapacity();

        // check capacities
        for (Event e : capacityCheck) {
          if (e.getCapacity() - totalTickets < 0) {
            errors.add("Die Ticketanzahl übersteigt die Saalkapazität.");
            break;
          }
        }

        for (TicketStatistic s : soldTickets) {
          if (capacity - s.getTicketsSold() - totalTickets < 0) {
            errors.add("Es gibt nicht genügend freie Plätze in dieser Veranstaltung.");
            break;
          }
        }
      }

    } catch (ResourceNotFoundException ex) {
      errors.add("Technischer Fehler 741.");
    }

    return errors;
  }

  @POST
  @Path("submit")
  public Boolean submit(ReservationDTO reservationDTO) {

    if (!validate(reservationDTO).isEmpty()) {
      return false;
    }

    Event parentEvent = events.get(reservationDTO.getEvent());
    Reservation parentReservation = convert(reservationDTO);
    parentReservation.setEvent(parentEvent);
    reservations.save(parentReservation);

    for (ReservationTicket t : convert(reservationDTO.getTickets())) {
      t.setReservation(parentReservation);
      reservationTickets.save(t);
    }

    if (reservationDTO.getExtras() != null) {
      for (Map.Entry<String, String> x : reservationDTO.getExtras().entrySet()) {
        ReservationExtra extra = new ReservationExtra();
        extra.setReservation(parentReservation);
        extra.setFieldName(x.getKey());
        extra.setValue(x.getValue());

        extras.save(extra);
      }
    }

    List<Event> childEvents = new LinkedList<>();

    if (reservationDTO.getChildEvents() != null) {
      for (Long cid : reservationDTO.getChildEvents()) {
        childEvents.add(events.get(cid));
      }

      for (Event e : childEvents) {
        Reservation currentReservation = convert(reservationDTO);
        currentReservation.setEvent(e);
        currentReservation.setParentReservation(parentReservation);
        currentReservation = reservations.save(currentReservation);

        for (ReservationTicket t : convert(reservationDTO.getTickets())) {
          t.setReservation(currentReservation);
          reservationTickets.save(t);
        }
      }
    }

    reservationChangedEvent.fireAsync(new ReservationChangedEvent(
            parentReservation, ReservationStatus.UNSET, ReservationStatus.NEW));

    return true;
  }

  private Reservation convert(ReservationDTO reservation) {
    Reservation result = new Reservation();
    result.setStatus(ReservationStatus.NEW);
    result.setCompany(reservation.getCompany());
    result.setGivenName(reservation.getGivenName());
    result.setSurname(reservation.getSurname());
    result.setAddress(reservation.getAddress());
    result.setPostcode(reservation.getPostcode());
    result.setCity(reservation.getCity());
    result.setPhone(reservation.getPhone());
    result.setEmail(reservation.getEmail());
    result.setMessage(reservation.getMessage());
    result.setTermsAccepted(true);
    return result;
  }

  private List<ReservationTicket> convert(Map<Long, Integer> tickets) {
    List<ReservationTicket> result = new LinkedList<>();
    for (Map.Entry<Long, Integer> t : tickets.entrySet()) {
      ReservationTicket ticket = new ReservationTicket();
      EventTicketPrice tprice = ticketPrices.get(t.getKey());
      ticket.setPrice(tprice.getPrice());
      ticket.setPriceCategory(tprice.getPriceCategory());
      ticket.setSeatCategory(tprice.getSeatCategory());
      ticket.setAmount(t.getValue());
      result.add(ticket);
    }
    return result;
  }
}
