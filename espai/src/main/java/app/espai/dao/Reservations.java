/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.filter.ReservationExtraFilter;
import app.espai.filter.ReservationFilter;
import app.espai.filter.ReservationTicketFilter;
import app.espai.model.Reservation;
import app.espai.model.ReservationExtra;
import app.espai.model.ReservationTicket;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import java.util.List;

/**
 * DAO class for reservation
 */
@Named
@Stateless
public class Reservations extends AbstractReservations {

  @EJB
  public ReservationTickets reservationTickets;

  @EJB
  public ReservationExtras reservationExtras;

  public void delete(Reservation reservation) {

    // delete child reservations
    ReservationFilter reservationFilter = new ReservationFilter();
    reservationFilter.setParentReservation(reservation);
    List<Reservation> childReservationList = list(reservationFilter).getItems();
    childReservationList.forEach(c -> delete(reservation));

    // delete tickets
    ReservationTicketFilter ticketFilter = new ReservationTicketFilter();
    ticketFilter.setReservation(reservation);
    List<ReservationTicket> tickets = reservationTickets.list(ticketFilter).getItems();
    for (ReservationTicket t : tickets) {
      reservationTickets.delete(t);
    }

    // delete extras
    ReservationExtraFilter extraFilter = new ReservationExtraFilter();
    extraFilter.setReservation(reservation);
    List<ReservationExtra> extras = reservationExtras.list(extraFilter).getItems();
    for (ReservationExtra e : extras) {
      reservationExtras.delete(e);
    }

    // delete reservation
    super.delete(reservation);
  }
}