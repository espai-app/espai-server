/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.businesslogic;

import app.espai.dao.HallCapacities;
import app.espai.dao.ReservationTickets;
import app.espai.dao.Reservations;
import app.espai.dao.SeatCategories;
import app.espai.filter.HallCapacityFilter;
import app.espai.filter.ReservationFilter;
import app.espai.filter.ReservationTicketFilter;
import app.espai.model.Event;
import app.espai.model.Hall;
import app.espai.model.HallCapacity;
import app.espai.model.Reservation;
import app.espai.model.ReservationStatus;
import app.espai.model.ReservationTicket;
import app.espai.model.SeatCategory;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class CapacityCalculator {

  @EJB
  private Reservations reservations;

  @EJB
  private ReservationTickets tickets;

  @EJB
  private SeatCategories seatCategories;

  @EJB
  private HallCapacities hallCapacities;

  public Map<Long, List<SeatCategory>> getSeats(List<Event> eventList) {

    // get reservations and sold tickets
    ReservationFilter reservationFilter = new ReservationFilter();
    reservationFilter.setEvents(eventList);
    reservationFilter.setStatuses(List.of(
            ReservationStatus.NEW,
            ReservationStatus.HOLD,
            ReservationStatus.CONFIRMED));
    List<Reservation> reservationList = reservations.list(reservationFilter).getItems();

    ReservationTicketFilter ticketFilter = new ReservationTicketFilter();
    ticketFilter.setReservations(reservationList);
    List<ReservationTicket> ticketList = tickets.list(ticketFilter).getItems();

    // get hall and hall capacity
    List<Hall> hallList = eventList.stream().map(Event::getHall).distinct().toList();

    HallCapacityFilter hallCapacityFilter = new HallCapacityFilter();
    hallCapacityFilter.setHalls(hallList);
    Map<Long, List<HallCapacity>> hallCapacityMap = hallCapacities.list(hallCapacityFilter)
            .getItems().stream().collect(Collectors.groupingBy(s -> s.getHall().getId()));

    Map<Long, List<SeatCategory>> result = new LinkedHashMap<>();

    for (Event event : eventList) {

      List<HallCapacity> hallCapacityList = hallCapacityMap.get(event.getHall().getId());
      List<SeatCategory> eventSeats = new LinkedList<>();

      if (event.getParentEvent() == null) {

        for (HallCapacity hallCapacity : hallCapacityList) {
          SeatCategory currentSeatCategory = hallCapacity.getSeatCategory().duplicate();

          Map<Long, Integer> soldTickets = ticketList.stream()
                  .filter(t -> t.getReservation().getEvent().equals(event))
                  .collect(
                          Collectors.toMap(
                                  t -> t.getSeatCategory().getId(),
                                  ReservationTicket::getAmount,
                                  (a1, a2) -> a1 + a2));

          currentSeatCategory.setCapacity((event.getCapacity() < hallCapacity.getCapacity())
                  ? event.getCapacity()
                  : hallCapacity.getCapacity());

          if (soldTickets.containsKey(currentSeatCategory.getId())) {
            currentSeatCategory.setSeatsTaken(soldTickets.get(currentSeatCategory.getId()));
            currentSeatCategory.setSeatsAvailable(
                    currentSeatCategory.getCapacity() - currentSeatCategory.getSeatsTaken());
          } else {
            currentSeatCategory.setSeatsTaken(0);
            currentSeatCategory.setSeatsAvailable(currentSeatCategory.getCapacity());
          }

          eventSeats.add(currentSeatCategory);
        }
      } else {
        int totalSeatsTaken = ticketList.stream().collect(
                Collectors.summingInt(ReservationTicket::getAmount));

        SeatCategory commonSeatCategory = new SeatCategory();
        commonSeatCategory.setName("Teilnehmer:innen");
        commonSeatCategory.setCapacity(event.getCapacity());
        commonSeatCategory.setSeatsTaken(totalSeatsTaken);
        commonSeatCategory.setSeatsAvailable(event.getCapacity() - totalSeatsTaken);
        eventSeats.add(commonSeatCategory);
      }

      result.put(event.getId(), eventSeats);
    }

    return result;
  }

  public List<SeatCategory> getSeats(Event event) {

    Map<Long, List<SeatCategory>> result = getSeats(List.of(event));
    if (result.get(event.getId()) == null) {
      return Collections.EMPTY_LIST;
    }

    return result.get(event.getId());
  }

  public List<String> checkCapacity(Event event, List<ReservationTicket> requestedTickets) {

    List<String> errors = new LinkedList<>();

    List<SeatCategory> seatStats = getSeats(event);
    Integer totalSeatsTaken = seatStats.stream()
            .collect(Collectors.summingInt(SeatCategory::getSeatsTaken));

    Integer totalSeatsRequested = requestedTickets.stream()
            .collect(Collectors.summingInt(ReservationTicket::getAmount));

    Integer totalSeats = totalSeatsTaken + totalSeatsRequested;

    // check if exceeds hall capacity
    if (event.getHall().getCapacity() < totalSeats) {
      errors.add("Die gewünschte Anzahl an Tickets übersteigt die Saalkapazität.");
      return errors;
    }

    if (event.getTicketLimit() != null && event.getTicketLimit() < totalSeats) {
      errors.add(String.format("Die Veranstaltung '%s' gibt es nur %s Plätze, davon sind %d Plätze bereits reserviert.",
              event.getProduction().getProduction().getTitle(),
              event.getCapacity(),
              totalSeatsTaken));
      return errors;
    }

    if (event.getParentEvent() == null) {
      Map<Long, Integer> seatsAvailable = seatStats.stream()
              .collect(Collectors.toMap(
                      SeatCategory::getId,
                      SeatCategory::getSeatsAvailable,
                      (s1, s2) -> s1 + s2));
      Map<Long, Integer> seatsRequested = requestedTickets.stream()
              .collect(Collectors.toMap(
                      t -> t.getSeatCategory().getId(),
                      ReservationTicket::getAmount,
                      (a1, a2) -> a1 + a2));

      for (Map.Entry<Long, Integer> sr : seatsRequested.entrySet()) {
        if (!seatsAvailable.containsKey(sr.getKey())) {
          errors.add(String.format(
                  "Die Platzkategorie existiert für die Veranstaltung '%s' nicht.",
                  event.getProduction().getProduction().getTitle()));
          continue;
        }

        if (seatsAvailable.get(sr.getKey()) < sr.getValue()) {
          errors.add(String.format(
                  "In der Platzkategorie sind leider nur %d Plätze verfügbar.",
                  seatsAvailable.get(sr.getKey())));
          continue;
        }
      }
    }

    return errors;
  }

}
