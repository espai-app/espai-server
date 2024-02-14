/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Event;
import app.espai.model.ReservationStatus;
import app.espai.model.TicketStatistic;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.LinkedList;
import java.util.List;

/**
 * DAO class for reservationSummary
 */
@Named
@Stateless
public class ReservationSummaries extends AbstractReservationSummaries {

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