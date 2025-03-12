package app.espai.webservice;

import app.espai.businesslogic.MovieImporter;
import app.espai.businesslogic.PriceManager;
import app.espai.businesslogic.VisionKinoMovieImportFilter;
import app.espai.dao.EventTicketPrices;
import app.espai.dao.Events;
import app.espai.dao.HallCapacities;
import app.espai.dao.Halls;
import app.espai.dao.PriceCategories;
import app.espai.dao.ProductionVersions;
import app.espai.dao.Productions;
import app.espai.dao.ReservationTickets;
import app.espai.dao.Reservations;
import app.espai.dao.Seasons;
import app.espai.dao.SeatCategories;
import app.espai.filter.EventFilter;
import app.espai.filter.EventTicketPriceFilter;
import app.espai.filter.EventTicketPriceTemplateFilter;
import app.espai.filter.SeatCategoryFilter;
import app.espai.model.Event;
import app.espai.model.EventTicketPrice;
import app.espai.model.HallCapacity;
import app.espai.model.ReservationTicket;
import app.espai.model.Season;
import app.espai.model.SeatCategory;
import jakarta.ejb.EJB;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rborowski
 */
@Path("maintenance")
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

  @EJB
  private Reservations reservations;

  @EJB
  private Halls halls;

  @EJB
  private SeatCategories seatCategories;

  @EJB
  private PriceManager priceManager;

  @EJB
  private MovieImporter movieImporter;

  @EJB
  private HallCapacities hallCapacities;

  @GET
  @Path("fixPrices")
  public String fixPrices(@QueryParam("seasonId") Long seasonId) {

    Season season = seasons.get(seasonId);
    if (season == null) {
      return "Season nicht gefunden.";
    }

    EventFilter eventFilter = new EventFilter();
    eventFilter.setSeason(season);
    eventFilter.setReservable(Boolean.TRUE);
    eventFilter.setParentEventIsNull(Boolean.TRUE);
    List<Event> eventList = events.list(eventFilter).getItems();

    int counter = 0;

    for (Event event : eventList) {

      EventTicketPriceFilter ticketPriceFilter = new EventTicketPriceTemplateFilter();
      ticketPriceFilter.setEvent(event);
      List<EventTicketPrice> existingTicketPrices = ticketPrices.list(ticketPriceFilter).getItems();
      if (!existingTicketPrices.isEmpty()) {
        continue;
      }

      List<EventTicketPrice> newTicketPrices = priceManager.getPrices(event);
      for (EventTicketPrice p : newTicketPrices) {
        ticketPrices.save(p);
        counter++;
      }
    }

    return String.format("%d Preise ergänzt", counter);
  }

  @POST
  @Path("importMovies")
  public String importMovies(@FormParam("url") String url, @FormParam("username") String username,
          @FormParam("password") String password) throws IOException {

    VisionKinoMovieImportFilter vikiFilter = new VisionKinoMovieImportFilter();
    vikiFilter.setConnection(url, username, password);
    List<MovieImport> movieList = vikiFilter.getMovies();

    for (MovieImport m : movieList) {
      if (m.getAttachments() == null) {
        continue;
      }
    }

    movieImporter.importMovies(movieList);

    return "Import beendet.";
  }

  @GET
  @Path("fixSeatCategories")
  public String fixSeatCategories() {

    Map<String, SeatCategory> seatCategoryMap = new HashMap<>();

    List<SeatCategory> seatCategoryList = seatCategories.list(new SeatCategoryFilter()).getItems();

    for (SeatCategory s : seatCategoryList) {
      if (!seatCategoryMap.containsKey(s.getName())) {
        seatCategoryMap.put(s.getName(), s);
      }
    }

    List<? extends HallCapacity> hallCapacityList = hallCapacities.list().getItems();
    for (HallCapacity h : hallCapacityList) {
      h.setSeatCategory(seatCategoryMap.get(h.getSeatCategory().getName()));
      hallCapacities.save(h);
    }

    List<? extends EventTicketPrice> eventTicketPriceList = ticketPrices.list().getItems();
    for (EventTicketPrice p : eventTicketPriceList) {
      if (p.getSeatCategory() != null) {
        p.setSeatCategory(seatCategoryMap.get(p.getSeatCategory().getName()));
        ticketPrices.save(p);
      }
    }

    List<? extends ReservationTicket> ticketList = tickets.list().getItems();
    for (ReservationTicket t : ticketList) {
      if (t.getSeatCategory() != null) {
        t.setSeatCategory(seatCategoryMap.get(t.getSeatCategory().getName()));
        tickets.save(t);
      }
    }

    for (SeatCategory s : seatCategoryList) {
      if (!seatCategoryMap.containsValue(s)) {
        seatCategories.delete(s);
      }
    }

    return "Kategorien zusammengefügt.";
  }
}
