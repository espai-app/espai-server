/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */
package app.espai.dao;

import app.espai.filter.EventFilter;
import app.espai.filter.ReservationFilter;
import app.espai.model.Event;
import app.espai.model.Production;
import app.espai.model.Reservation;
import app.espai.model.Season;
import app.espai.model.Venue;
import app.espai.model.VenueEventStatistic;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * DAO class for event
 */
@Named
@Stateless
public class Events extends AbstractEvents {

  @EJB
  private Reservations reservations;

  public static final Comparator<Event> DEFAULT_ORDER = new Comparator<>() {

    @Override
    public int compare(Event o1, Event o2) {
      String order1 = LocalDateTime.of(o1.getDate(), o1.getTime()).toString();
      if (o1.getParentEvent() != null) {
        order1 += "-" + String.valueOf(o1.getParentEvent().getId());
      }
      order1 += "-" + String.valueOf(o1.getId());

      String order2 = LocalDateTime.of(o2.getDate(), o2.getTime()).toString();
      if (o2.getParentEvent() != null) {
        order2 += "-" + String.valueOf(o2.getParentEvent().getId());
      }
      order2 += "-" + String.valueOf(o2.getId());

      return String.CASE_INSENSITIVE_ORDER.compare(order1, order2);
    }
  };

  public List<Production> listDistinctProductions(Season season) {
    TypedQuery<Production> query = em.createQuery(
            "SELECT DISTINCT e.production.production FROM Event e "
            + "WHERE e.season = :season AND e.parentEvent IS NULL",
            Production.class);
    query.setParameter("season", season);
    List<Production> productionList = query.getResultList();
    productionList.sort(Productions.DEFAULT_ORDER);

    return productionList;
  }

  public List<VenueEventStatistic> getNumberOfEvents(List<Venue> venues, Season season) {

    List<VenueEventStatistic> result = new LinkedList<>();

    for (Venue v : venues) {
      TypedQuery<Long> query = em.createQuery(
              "SELECT COUNT(e) "
              + "FROM Event e "
              + "WHERE e.hall.venue = :venue AND e.season = :season",
              Long.class);
      query.setParameter("venue", v);
      query.setParameter("season", season);

      Long count = query.getSingleResult();
      result.add(new VenueEventStatistic(v.getId(), v.getName(), v.getCity(), count));
    }

    return result;
  }

  public void delete(Event event) {

    // delete child events
    EventFilter childEventFilter = new EventFilter();
    childEventFilter.setParentEvent(event);
    List<Event> childEventList = list(childEventFilter).getItems();
    for (Event e : childEventList) {
      delete(e);
    }

    // delete reservations
    ReservationFilter reservationFilter = new ReservationFilter();
    reservationFilter.setEvent(event);

    List<Reservation> reservationList = reservations.list(reservationFilter).getItems();
    for (Reservation r : reservationList) {
      reservations.delete(r);
    }

    // delete event
    super.delete(event);
  }

}
