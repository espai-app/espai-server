/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Event;
import app.espai.model.ReservationStatus;
import app.espai.model.ReservationSummary;
import app.espai.model.TicketStatistic;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * DAO class for reservationSummary
 */
@Named
@Stateless
public class ReservationSummaries extends AbstractReservationSummaries {

  public static final Comparator<ReservationSummary> DEFAULT_ORDER
          = new Comparator<>() {

    @Override
    public int compare(ReservationSummary o1, ReservationSummary o2) {

      String order1 = LocalDateTime.of(o1.getEventDate(), o1.getEventStartTime())
                      .toString();
      if (o1.getParentReservation() != null) {
        order1 = toSortString(o1.getParentReservation().getStatus()) + "-"
                + order1 + "-" + String.valueOf(o1.getParentReservation().getId());
      } else {
        order1 = toSortString(o1.getStatus()) + "-" + order1;
      }
      order1 += "-" + String.valueOf(o1.getId());

      String order2 = LocalDateTime.of(o2.getEventDate(), o2.getEventStartTime())
                      .toString();
      if (o2.getParentReservation() != null) {
        order2 = toSortString(o2.getParentReservation().getStatus()) + "-"
                + order2 + "-" + String.valueOf(o2.getParentReservation().getId());
      } else {
        order2 = toSortString(o2.getStatus()) + "-" + order2;
      }
      order2 += "-" + String.valueOf(o2.getId());

      return String.CASE_INSENSITIVE_ORDER.compare(order1, order2);
    }
  };

  private static String toSortString(ReservationStatus status) {
    return switch(status) {
      case NEW -> "1";
      case HOLD -> "2";
      case CONFIRMED -> "3";
      case CANCELED -> "4";
      default -> "1";
    };
  }

  public List<TicketStatistic> getTicketSales(List<Event> events) {

    if (events == null || events.isEmpty()) {
      return new LinkedList<>();
    }

    TypedQuery query  = em.createQuery(
            "SELECT new app.espai.model.TicketStatistic(s.event, SUM(s.tickets)) "
                    + "FROM ReservationSummary s "
                    + "WHERE s.event IN :events AND s.status != :cancelStatus "
                    + "GROUP BY s.event", TicketStatistic.class);
    query.setParameter("events", events);
    query.setParameter("cancelStatus", ReservationStatus.CANCELED);

    return query.getResultList();
  }


}