package app.espai.webservice;

import app.espai.businesslogic.PriceManager;
import app.espai.dao.Attachments;
import app.espai.dao.EventTicketPrices;
import app.espai.dao.Events;
import app.espai.dao.HallCapacities;
import app.espai.dao.LicenseConditions;
import app.espai.dao.ProductionVersions;
import app.espai.dao.Productions;
import app.espai.dao.ReservationTickets;
import app.espai.dao.SeasonProductions;
import app.espai.dao.SeasonVenues;
import app.espai.dao.Seasons;
import app.espai.dao.SeatCategories;
import app.espai.dao.Venues;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.EventFilter;
import app.espai.filter.EventTicketPriceFilter;
import app.espai.filter.EventTicketPriceTemplateFilter;
import app.espai.filter.LicenseConditionFilter;
import app.espai.filter.SeasonProductionFilter;
import app.espai.filter.SeasonVenueFilter;
import app.espai.filter.SeatCategoryFilter;
import app.espai.filter.VenueFilter;
import app.espai.model.Attachment;
import app.espai.model.AttachmentType;
import app.espai.model.Event;
import app.espai.model.EventTicketPrice;
import app.espai.model.HallCapacity;
import app.espai.model.LicenseCondition;
import app.espai.model.Production;
import app.espai.model.ProductionVersion;
import app.espai.model.ReservationTicket;
import app.espai.model.Season;
import app.espai.model.SeasonProduction;
import app.espai.model.SeasonVenue;
import app.espai.model.SeatCategory;
import app.espai.model.Venue;
import jakarta.ejb.EJB;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author rborowski
 */
@Path("maintenance")
public class MaintenanceWebservice {

  @EJB
  private Attachments attachments;

  @EJB
  private Seasons seasons;

  @EJB
  private SeasonVenues seasonVenues;

  @EJB
  private SeasonProductions seasonProductions;

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
  private Venues venues;

  @EJB
  private SeatCategories seatCategories;

  @EJB
  private PriceManager priceManager;

  @EJB
  private HallCapacities hallCapacities;

  @EJB
  private LicenseConditions licenses;

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

  @GET
  @Path("createDiscussions")
  public String createDiscussions(
          @QueryParam("seasonId") Long seasonId,
          @QueryParam("discussionId") Long discussionId) {

    if (seasonId == null || discussionId == null) {
      throw new BadRequestException("Params missing");
    }

    Season season = seasons.get(seasonId);
    if (season == null) {
      throw new BadRequestException("Season does not exist.");
    }

    ProductionVersion discussion = productionVersions.get(discussionId);
    if (season == null) {
      throw new BadRequestException("Discussion does not exist.");
    }

    EventFilter eventFilter = new EventFilter();
    eventFilter.setSeason(season);

    List<Event> eventList = events.list(eventFilter).getItems();

    List<Long> parentEvents = new LinkedList<>();
    for (Event e : eventList) {
      if (e.getParentEvent() != null) {
        parentEvents.add(e.getId());
      }
    }

    int counter = 0;
    for (Event e : eventList) {
      if (e.getParentEvent() != null || parentEvents.contains(e.getId())) {
        continue;
      }

      Event d = new Event();
      d.setDate(e.getDate());
      d.setTime(e.getTime());
      d.setSeason(season);
      d.setParentEvent(e);
      d.setProduction(discussion);
      d.setReservable(true);
      d.setHall(e.getHall());

      events.save(d);
      counter++;
    }

    return String.format("%d Filmgespräche hinzugefügt.", counter);
  }

  @GET
  @Path("fixPosters")
  @Produces("text/plain; charset=UTF-8")
  public String fixPosters() {

    int total = 0;
    int updated = 0;

    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setMediaType(AttachmentType.IMAGE);
    List<Attachment> attachmentList = attachments.list(attachmentFilter).getItems();

    for (Attachment a : attachmentList) {

      total++;

      if (a.getCopyright() != null && !a.getCopyright().isBlank()) {
        if (!a.getCopyright().contains("©")) {
          continue;
        }

        a.setCopyright(a.getCopyright().replace("©", "").trim());
      } else {
        if (!a.getEntityType().equals("Movie")) {
          continue;
        }

        Production p = productions.get(a.getEntityId());
        if (p == null) {
          continue;
        }

        LicenseConditionFilter licenseFilter = new LicenseConditionFilter();
        licenseFilter.setProduction(p);
        List<LicenseCondition> licenseList = licenses.list(licenseFilter).getItems();

        if (licenseList.isEmpty() || licenseList.get(0).getAgency() == null) {
          continue;
        }

        a.setCopyright(licenseList.get(0).getAgency().getName());
      }

      attachments.save(a);
      updated++;
    }

    return String.format("%d von %d aktualisiert.", updated, total);
  }

  @GET
  @Path("copy/")
  @Produces("text/plain; charset=UTF-8")
  public String copyEvents(@QueryParam("source") Long source,
          @QueryParam("target") Long target) {

      Season sourceSeason = seasons.get(source);
      Season targetSeason = seasons.get(target);

      SeasonVenueFilter seasonVenueFilter = new SeasonVenueFilter();
      seasonVenueFilter.setSeason(sourceSeason);
      List<SeasonVenue> sourceVenues = seasonVenues
              .list(seasonVenueFilter).getItems();

      int venuesAdded = 0;
      for (SeasonVenue s : sourceVenues) {
          SeasonVenue t = new SeasonVenue();
          t.setSeason(targetSeason);
          t.setVenue(s.getVenue());

          seasonVenues.save(t);

          venuesAdded++;
      }

      SeasonProductionFilter seasonProductionFilter = new SeasonProductionFilter();
      seasonProductionFilter.setSeason(sourceSeason);
      List<SeasonProduction> sourceProductions = seasonProductions
              .list(seasonProductionFilter).getItems();

      int productionsAdded = 0;
      for (SeasonProduction s : sourceProductions) {
          SeasonProduction t = new SeasonProduction();
          t.setSeason(targetSeason);
          t.setProduction(s.getProduction());

          seasonProductions.save(t);
          productionsAdded++;
      }

      EventFilter eventFilter = new EventFilter();
      eventFilter.setSeason(sourceSeason);
      List<Event> sourceEvents = events.list(eventFilter).getItems();

      int eventsAdded = 0;
      for (Event s : sourceEvents) {
          Event t = new Event();
          t.setSeason(targetSeason);
          t.setDate(s.getDate().plusYears(1));
          t.setTime(s.getTime());
          t.setProduction(s.getProduction());
          t.setHall(s.getHall());
          t.setReservationForm(s.getReservationForm());

          events.save(t);

          eventsAdded++;
      }

      return String.format(
              "%d venues copied, %d productions copied, %d events copied.",
              venuesAdded, productionsAdded, eventsAdded);
  }

  @GET
  @Path("venueSlugs")
  @Produces("text/plain; charset=UTF-8")
  public String setSlugs() {

    VenueFilter filter = new VenueFilter();
    filter.setSlugIsNull(Boolean.TRUE);
    List<Venue> venueList = venues.list(filter).getItems();

    int counter = 0;

    for (Venue v : venueList) {
      String slug = v.getName().trim().replaceAll("\\W", "-").toLowerCase(Locale.GERMAN);
      if (v.getCity() != null && !v.getCity().isBlank()) {
        slug += "-" + v.getCity().trim().replaceAll("\\W", "-").toLowerCase(Locale.GERMAN);
      }

      slug = slug.replaceAll("-{2,}", "-");

      v.setSlug(slug);
      venues.save(v);
      counter++;
    }

    return String.format("%d slugs added.", counter);
  }
}
