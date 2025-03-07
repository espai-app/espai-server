package app.espai.webservice;

import app.espai.businesslogic.CapacityCalculator;
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
import app.espai.dto.EventDTOConverter;
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

  @Inject
  private CapacityCalculator capacityCalculator;

  @GET
  @Path("{id}")

  public Response get(@PathParam("seasonId") Long seasonId, @PathParam("id") long id) {

    Event event = events.get(id);
    if (!event.getSeason().getId().equals(seasonId)) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    EventDTO result = toDTO(event);
    addChildEvents(event, result);

    return Response.ok(result).build();
  }

  @GET
  public List<EventDTO> list(
          @PathParam("seasonId") Long seasonId,
          @QueryParam("venueId") Long venueId,
          @QueryParam("productionId") Long productionId,
          @QueryParam("eventSerialId") Long serialId,
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

    List<EventDTO> eventDTOList = new LinkedList<>();
    for (Event e : eventList) {
      EventDTO target = toDTO(e);
      addChildEvents(e, target);
      eventDTOList.add(target);
    }

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

    return EventDTOConverter.of(event, attachmentList, getPriceList(event), capacityCalculator.getSeats(event));
  }

  private void addChildEvents(Event parentEvent, EventDTO target) {
    EventFilter childEventFilter = new EventFilter();
    childEventFilter.setParentEvent(parentEvent);
    childEventFilter.setHidden(false);

    List<Event> childEventList = events.list(childEventFilter).getItems();
    target.setChildEvents(childEventList.stream()
            .map(e-> EventDTOConverter.of(e, null, null, capacityCalculator.getSeats(e)))
            .toList());
  }

  private List<EventTicketPrice> getPriceList(Event event) {
    EventTicketPriceFilter pricesFilter = new EventTicketPriceFilter();
    pricesFilter.setEvent(event);
    return ticketPrices.list(pricesFilter).getItems();
  }

}
