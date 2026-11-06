package app.espai.webservice.season;

import app.espai.dao.EventSerials;
import app.espai.dao.Events;
import app.espai.dao.Halls;
import app.espai.dao.ProductionTypes;
import app.espai.dao.ProductionVersions;
import app.espai.dao.Productions;
import app.espai.dao.Seasons;
import app.espai.dao.Venues;
import app.espai.dto.EventDTOConverter;
import app.espai.filter.EventFilter;
import app.espai.filter.HallFilter;
import app.espai.filter.ProductionFilter;
import app.espai.filter.ProductionVersionFilter;
import app.espai.model.Event;
import app.espai.model.Production;
import app.espai.model.Season;
import app.espai.model.Venue;
import app.espai.sdk.model.EventDTO;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;
import rocks.xprs.runtime.db.Range;

/**
 *
 * @author rborowski
 */
@Path("/season/{seasonId}/events")
@Produces(MediaType.APPLICATION_JSON)
public class SeasonEventWebservice {

  @EJB
  private Events events;

  @EJB
  private Seasons seasons;

  @EJB
  private Venues venues;

  @EJB
  private Halls halls;

  @EJB
  private Productions productions;

  @EJB
  private ProductionVersions productionVersions;

  @EJB
  private ProductionTypes productionTypes;

  @EJB
  private EventSerials eventSerials;

  @Inject
  private EventDTOConverter eventConverter;

  @GET
  @Path("{id}")
  public Response get(@PathParam("seasonId") Long seasonId, @PathParam("id") long id) {

    Event event = events.get(id);
    if (!event.getSeason().getId().equals(seasonId)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    EventDTO result = eventConverter.toDTO(event);

    return Response.ok(result).build();
  }

  @GET
  public List<EventDTO> list(
          @PathParam("seasonId") Long seasonId,
          @QueryParam("venueId") String venueSlug,
          @QueryParam("productionId") Long productionId,
          @QueryParam("eventSerialId") Long serialId,
          @QueryParam("productionTypeId") Long productionTypeId,
          @QueryParam("date") String dateString,
          @QueryParam("showPast") Boolean showPast) {

    EventFilter eventFilter = new EventFilter();

    Season season = seasons.get(seasonId);
    eventFilter.setSeason(season);
    eventFilter.setParentEventIsNull(true);

    // filter by date
    if (showPast == null || !showPast) {
      eventFilter.setDate(new Range<>(LocalDate.now(), null));
    }

    // filter by venue
    if (venueSlug != null) {
      Venue venue = venues.getBySlug(venueSlug);

      HallFilter hallFilter = new HallFilter();
      hallFilter.setVenue(venue);
      eventFilter.setHalls(halls.list(hallFilter).getItems());
    }

    // filter by main show?
    if (productionId != null) {

      Production production = productions.get(productionId);

      ProductionVersionFilter versionFilter = new ProductionVersionFilter();
      versionFilter.setProduction(production);

      eventFilter.setProductions(productionVersions.list(versionFilter).getItems());
    }

    // filter by event serial?
    if (serialId != null) {
      eventFilter.setEventSerial(eventSerials.get(serialId));
    }

    // filter by production type?
    if (productionTypeId != null) {
      // TODO simplify
      ProductionFilter productionFilter = new ProductionFilter();
      productionFilter.setProductionType(productionTypes.get(productionTypeId));
      List<Production> productionList = productions.list(productionFilter).getItems();

      ProductionVersionFilter versionFilter = new ProductionVersionFilter();
      versionFilter.setProductions(productionList);

      eventFilter.setProductions(productionVersions.list(versionFilter).getItems());
    }

    // filter by date?
    if (dateString != null && dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
      LocalDate date = LocalDate.parse(dateString);
      eventFilter.setDate(new Range(date, date));
    }

    eventFilter.setHidden(false);
    eventFilter.setOrderBy("date");
    eventFilter.setOrder(PageableFilter.Order.ASC);

    List<Event> eventList = events.list(eventFilter).getItems();

    if (eventList.isEmpty()) {
      return new LinkedList<>();
    }

    eventList.sort(Events.DEFAULT_ORDER);

    if ("auto".equals(dateString)) {
      final LocalDate filterDate;
      if (eventList.get(0).getDate().isAfter(LocalDate.now())) {
        filterDate = eventList.get(0).getDate();
      } else {
        filterDate = LocalDate.now();
      }

      eventList = eventList.stream().filter(e -> e.getDate().equals(filterDate)).toList();
    }

    List<EventDTO> eventDTOList = eventList.stream().map(e -> eventConverter.toDTO(e)).toList();

    return eventDTOList;
  }
}
