package app.espai.webservice;

import app.espai.dao.Attachments;
import app.espai.dao.EventSerials;
import app.espai.dao.EventTicketPrices;
import app.espai.dao.Events;
import app.espai.dao.Halls;
import app.espai.dao.ProductionVersions;
import app.espai.dao.Productions;
import app.espai.dao.ReservationSummaries;
import app.espai.dao.Seasons;
import app.espai.dao.Venues;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.EventFilter;
import app.espai.filter.EventTicketPriceFilter;
import app.espai.filter.HallFilter;
import app.espai.filter.ProductionVersionFilter;
import app.espai.model.Attachment;
import app.espai.model.Event;
import app.espai.model.EventTicketPrice;
import app.espai.model.Production;
import app.espai.model.Season;
import app.espai.model.Venue;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Path("/season/{seasonId}/events")
@Produces(MediaType.APPLICATION_JSON)
public class EventWebservice {

  @EJB
  private Attachments attachments;

  @EJB
  private Events events;

  @EJB
  private EventTicketPrices ticketPrices;

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
  private ReservationSummaries reservationSummaries;

  @EJB
  private EventSerials eventSerials;

  @GET
  @Path("{id}")

  public Response get(@PathParam("seasonId") Long seasonId, @PathParam("id") long id) {

    Event event = events.get(id);
    if (!event.getSeason().getId().equals(seasonId)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(toDTO(event)).build();
  }

  @GET
  public List<EventDTO> list(
          @PathParam("seasonId") Long seasonId,
          @QueryParam("venueId") Long venueId,
          @QueryParam("productionId") Long productionId,
          @QueryParam("eventSerialId") Long serialId) {

    EventFilter eventFilter = new EventFilter();

    Season season = seasons.get(seasonId);
    eventFilter.setSeason(season);
    eventFilter.setParentEventIsNull(true);

    // filter by venue
    if (venueId != null) {
      Venue venue = venues.get(venueId);

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

    eventFilter.setHidden(false);
    eventFilter.setOrderBy("date");
    eventFilter.setOrder(PageableFilter.Order.ASC);

    List<Event> eventList = events.list(eventFilter).getItems();

    if (eventList.isEmpty()) {
      return new LinkedList<>();
    }

    eventList.sort(Events.DEFAULT_ORDER);

    Map<Long, Integer> ticketsSold = new HashMap<>();
    reservationSummaries.getTicketSales(eventList)
            .stream()
            .forEach(e -> ticketsSold.put(e.getEvent().getId(), e.getTicketsSold().intValue()));

    List<EventDTO> eventDTOList = eventList
            .stream()
            .map(e -> EventDTO.of(e, null, null, null, ticketsSold))
            .toList();
    return eventDTOList;
  }

  private EventDTO toDTO(Event event) {

    EventFilter childEventFilter = new EventFilter();
    childEventFilter.setParentEvent(event);
    childEventFilter.setHidden(Boolean.FALSE);

    List<Event> childEventList = events.list(childEventFilter).getItems();
    childEventList.sort(Events.DEFAULT_ORDER);

    Production production = event.getProduction().getProduction();
    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityId(production.getId());
    attachmentFilter.setEntityType(production.getClass().getSimpleName());
    List<Attachment> attachmentList = attachments.list(attachmentFilter).getItems();

    List<Event> allEvents = new LinkedList<>(childEventList);
    allEvents.add(event);

    EventTicketPriceFilter priceFilter = new EventTicketPriceFilter();
    priceFilter.setEvents(allEvents);

    List<EventTicketPrice> priceList = ticketPrices.list(priceFilter).getItems();
    Map<Long, List<EventTicketPrice>> priceMap = new HashMap<>();
    for (EventTicketPrice p : priceList) {
      if (!priceMap.containsKey(p.getEvent().getId())) {
        priceMap.put(p.getEvent().getId(), new LinkedList<>());
      }
      priceMap.get(p.getEvent().getId()).add(p);
    }

    Map<Long, Integer> ticketsSold = new HashMap<>();
    reservationSummaries.getTicketSales(allEvents)
            .stream()
            .forEach(e -> ticketsSold.put(e.getEvent().getId(), e.getTicketsSold().intValue()));

    return EventDTO.of(event, childEventList, attachmentList, priceMap, ticketsSold);
  }

}
