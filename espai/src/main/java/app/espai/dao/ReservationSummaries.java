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
      long order1;
      long order2;
      
      if (o1.getParentReservation() != null) {
        order1 = (toSortPrefix(o1.getParentReservation().getStatus()) * 100000000000000L) 
                + o1.getParentReservation().getCreateDate().getTime() - 1L;
      } else {
        order1 = (toSortPrefix(o1.getStatus()) * 100000000000000L) 
                + o1.getCreateDate().getTime();
      }
      
      if (o2.getParentReservation() != null) {
        order2 = (toSortPrefix(o2.getParentReservation().getStatus()) * 100000000000000L) 
                + o2.getParentReservation().getCreateDate().getTime() - 1L;
      } else {
        order2 = (toSortPrefix(o2.getStatus()) * 100000000000000L) 
                + o2.getCreateDate().getTime();
      }
      
      long diff = order2 - order1;
      if (diff == 0L) {
        return 0;
      }
      
      return diff > 0L ? 1 : -1;
    }
  };

  private static long toSortPrefix(ReservationStatus status) {
    return switch(status) {
      case NEW -> 4;
      case HOLD -> 3;
      case CONFIRMED -> 2;
      case CANCELED -> 1;
      default -> 4;
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