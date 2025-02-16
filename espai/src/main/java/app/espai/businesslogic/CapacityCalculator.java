/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.businesslogic;

import app.espai.dao.ReservationTickets;
import app.espai.dao.Reservations;
import app.espai.dao.SeatCategories;
import app.espai.filter.ReservationFilter;
import app.espai.filter.ReservationTicketFilter;
import app.espai.filter.SeatCategoryFilter;
import app.espai.model.Event;
import app.espai.model.Reservation;
import app.espai.model.ReservationStatus;
import app.espai.model.ReservationTicket;
import app.espai.model.SeatCategory;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

  public List<SeatCategory> getSeats(Event event) {

    ReservationFilter reservationFilter = new ReservationFilter();
    reservationFilter.setEvent(event);
    reservationFilter.setStatuses(List.of(
            ReservationStatus.NEW,
            ReservationStatus.HOLD,
            ReservationStatus.CONFIRMED));
    List<Reservation> reservationList = reservations.list(reservationFilter).getItems();

    ReservationTicketFilter ticketFilter = new ReservationTicketFilter();
    ticketFilter.setReservations(reservationList);
    List<ReservationTicket> ticketList = tickets.list(ticketFilter).getItems();

    SeatCategoryFilter seatCategoryFilter = new SeatCategoryFilter();
    seatCategoryFilter.setHall(event.getHall());
    List<SeatCategory> seatCategoryList;
    
    if (event.getParentEvent() == null) {
      seatCategoryList = seatCategories.list(seatCategoryFilter).getItems();

      for (SeatCategory currentSeatCategory : seatCategoryList) {
        
        Map<SeatCategory, Integer> soldTickets = ticketList.stream().collect(
            Collectors.toMap(
                    ReservationTicket::getSeatCategory,
                    ReservationTicket::getAmount,
                    (a1, a2) -> a1 + a2));

        int capacity = (event.getCapacity() < currentSeatCategory.getCapacity()) 
                ? event.getCapacity() 
                : currentSeatCategory.getCapacity();

        if (soldTickets.containsKey(currentSeatCategory)) {
          currentSeatCategory.setSeatsTaken(soldTickets.get(currentSeatCategory));
          currentSeatCategory.setSeatsAvailable(
                  capacity - currentSeatCategory.getSeatsTaken());
        } else {
          currentSeatCategory.setSeatsTaken(0);
          currentSeatCategory.setSeatsAvailable(capacity);
        }
      }
    } else {
      int totalSeatsTaken = ticketList.stream().collect(
            Collectors.summingInt(ReservationTicket::getAmount));

      seatCategoryList = new LinkedList<>();
      SeatCategory commonSeatCategory = new SeatCategory();
      commonSeatCategory.setName("Teilnehmer:innen");
      commonSeatCategory.setCapacity(event.getCapacity());
      commonSeatCategory.setSeatsTaken(totalSeatsTaken);
      commonSeatCategory.setSeatsAvailable(event.getCapacity() - totalSeatsTaken);
      seatCategoryList.add(commonSeatCategory);
    }

    return seatCategoryList;
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
      Map<SeatCategory, Integer> seatsAvailable = seatStats.stream()
              .collect(Collectors.toMap(
                      Function.identity(),
                      SeatCategory::getSeatsAvailable,
                      (s1, s2) -> s1 + s2));
      Map<SeatCategory, Integer> seatsRequested = requestedTickets.stream()
              .collect(Collectors.toMap(
                      ReservationTicket::getSeatCategory,
                      ReservationTicket::getAmount,
                      (a1, a2) -> a1 + a2));

      for (Map.Entry<SeatCategory, Integer> sr : seatsRequested.entrySet()) {
        if (!seatsAvailable.containsKey(sr.getKey())) {
          errors.add(String.format(
                  "Die Platzkategorie %s existiert für die Veranstaltung '%s' nicht.", 
                  event.getProduction().getProduction().getTitle(),
                  sr.getKey().getName()));
          continue;
        } 

        if (seatsAvailable.get(sr.getKey()) < sr.getValue()) {
          errors.add(String.format(
                  "In der Platzkategorie %s sind leider nur %d Plätze verfügbar.", 
                  sr.getKey().getName(),
                  seatsAvailable.get(sr.getKey())));
          continue;
        }
      }
    }
    
    return errors;
  }

}
