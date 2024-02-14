package app.espai.webservice;

import app.espai.dao.EventTicketPrices;
import app.espai.dao.Events;
import app.espai.dao.PriceCategories;
import app.espai.dao.ProductionVersions;
import app.espai.dao.Productions;
import app.espai.dao.ReservationTickets;
import app.espai.dao.Seasons;
import app.espai.filter.EventFilter;
import app.espai.filter.EventTicketPriceFilter;
import app.espai.model.Event;
import app.espai.model.EventTicketPrice;
import app.espai.model.PriceCategory;
import app.espai.model.ProductionVersion;
import app.espai.model.Season;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.util.List;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
@Path("maintainance")
public class MaintenanceWebservice {

  @EJB
  private PriceCategories priceCategories;

  @EJB
  private Seasons seasons;

  @EJB
  private Productions productions;

  @EJB
  private ProductionVersions productionVersions;

  @EJB
  private Events events;

  @EJB
  private EventTicketPrices ticketPrices;

  @EJB
  private ReservationTickets tickets;

  @GET
  @Path("fixPriceCategories")
  public String fixPriceCategories() {
    if (priceCategories.list().getTotalNumberOfResults() == 0) {

      PriceCategory p1 = new PriceCategory();
      p1.setName("Schüler:innen");
      priceCategories.save(p1);

      PriceCategory p2 = new PriceCategory();
      p2.setName("Lehrer:innen");
      priceCategories.save(p2);

      return "Erstellt.";
    }

    return "Nichts erstellt.";
  }

  @GET
  @Path("fixPrices")
  public String fixPrices() {

    List<? extends PriceCategory> priceCatList = priceCategories.list().getItems();
    PriceCategory student = null;
    PriceCategory teacher = null;

    for (PriceCategory p : priceCatList) {
      if (p.getName().equals("Schüler:innen")) {
        student = p;
      } else {
        teacher = p;
      }
    }

    if (student == null || teacher == null) {
      return "Preiskategorien unvollständig.";
    }

    List<? extends Event> eventList = events.list().getItems();

    int counter = 0;
    for (Event e : eventList) {

      EventTicketPriceFilter filter = new EventTicketPriceFilter();
      filter.setEvent(e);

      List<EventTicketPrice> prices = ticketPrices.list(filter).getItems();

      if (prices.isEmpty()) {
        EventTicketPrice ps = new EventTicketPrice();
        ps.setEvent(e);
        ps.setPriceCategory(student);
        ps.setPrice(new Monetary("4.50", "EUR"));
        ticketPrices.save(ps);

        EventTicketPrice pt = new EventTicketPrice();
        pt.setEvent(e);
        pt.setPriceCategory(teacher);
        pt.setPrice(new Monetary(BigDecimal.ZERO, "EUR"));
        ticketPrices.save(pt);

        counter++;
      }
    }

    return counter + " Events ergaenzt.";
  }

  @GET
  @Path("addRequiredChildren")
  public String addChildEvents(
          @QueryParam("seasonId") long seasonId,
          @QueryParam("productionVersion") long productionVersionId,
          @QueryParam("childVersion") long childId,
          @QueryParam("mandatory") boolean mandatory) {

    int counter = 0;

    Season season = seasons.get(seasonId);
    ProductionVersion main = productionVersions.get(productionVersionId);
    ProductionVersion child = productionVersions.get(childId);

    EventFilter eventFilter = new EventFilter();
    eventFilter.setSeason(season);
    eventFilter.setProduction(main);

    List<Event> eventList = events.list(eventFilter).getItems();
    for (Event e : eventList) {
      boolean alreadyCreated = false;

      EventFilter childFilter = new EventFilter();
      childFilter.setParentEvent(e);

      List<Event> childEventList = events.list(childFilter).getItems();
      for (Event c : childEventList) {
        if (c.getProduction().getId() == childId) {
          alreadyCreated = true;
          break;
        }
      }

      if (!alreadyCreated) {
        Event c = new Event();
        c.setSeason(season);
        c.setParentEvent(e);
        c.setDate(e.getDate());
        c.setTime(e.getTime());
        c.setHall(e.getHall());
        c.setProduction(child);
        c.setMandatory(mandatory);
        events.save(c);

        counter++;
      }
    }

    return "%d Einträge erstellt".formatted(counter);
  }

}
