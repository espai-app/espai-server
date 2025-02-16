package app.espai.webservice;

import app.espai.businesslogic.CapacityCalculator;
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
import app.espai.sdk.model.ReservationDTO;
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
  
  @Inject
  private CapacityCalculator capacityCalculator;
  
  @POST
  @Path("checkTicketAvailability")
  public List<String> checkTicketAvailability(ReservationDTO reservation) {
    List<String> errors = new LinkedList<>();

    try {
      List<Event> eventsToCheck = new LinkedList<>();

      // check if event exists
      Event event = events.get(reservation.getEvent());
      eventsToCheck.add(event);
      
      List<ReservationTicket> requestedTickets = convert(reservation.getTickets());

      if (reservation.getChildEvents() != null && !reservation.getChildEvents().isEmpty()) {
        for (Long cid : reservation.getChildEvents()) {
          eventsToCheck.add(events.get(cid));
        }
      }

      for (Event e : eventsToCheck) {
        if (!e.isReservable()) {
          errors.add(String.format("Die Veranstaltung '%s' kann nicht gebucht werden.", 
                  e.getProduction().getProduction().getTitle()));
          continue;
        }
        
        errors.addAll(capacityCalculator.checkCapacity(e, requestedTickets));
      }

    } catch (ResourceNotFoundException ex) {
      errors.add("Technischer Fehler 741.");
    }

    return errors;
  }

  @POST
  @Path("validate")
  public List<String> validate(ReservationDTO reservation) {

    List<String> errors = new LinkedList<>();

    try {
      List<Event> eventsToCheck = new LinkedList<>();

      // check if event exists
      Event event = events.get(reservation.getEvent());
      eventsToCheck.add(event);
      
      List<ReservationTicket> requestedTickets = convert(reservation.getTickets());

      if (reservation.getChildEvents() != null && !reservation.getChildEvents().isEmpty()) {
        for (Long cid : reservation.getChildEvents()) {
          eventsToCheck.add(events.get(cid));
        }
      }

      for (Event e : eventsToCheck) {
        if (!e.isReservable()) {
          errors.add(String.format("Die Veranstaltung '%s' kann nicht gebucht werden.", 
                  e.getProduction().getProduction().getTitle()));
          continue;
        }
        
        errors.addAll(capacityCalculator.checkCapacity(e, requestedTickets));
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
