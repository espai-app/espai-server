package app.espai.webservice;

import app.espai.dao.Events;
import app.espai.dao.HallCapacities;
import app.espai.dao.Halls;
import app.espai.dao.ProductionVersions;
import app.espai.dao.ReservationExtras;
import app.espai.dao.ReservationTickets;
import app.espai.dao.Reservations;
import app.espai.dao.SeasonPriceTemplates;
import app.espai.dao.SeasonVenueProductions;
import app.espai.dao.Seasons;
import app.espai.dao.Venues;
import app.espai.dto.EventDTOConverter;
import app.espai.model.Event;
import app.espai.model.EventStatus;
import app.espai.model.Hall;
import app.espai.model.ProductionVersion;
import app.espai.model.Season;
import app.espai.sdk.model.EventRequestDTO;
import app.espai.sdk.model.ValidationError;
import app.espai.sdk.model.ValidationErrorList;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Stateless
@Path("season/{seasonId}/requestEvent")
public class EventRequestWebservice {

  @EJB
  private Seasons seasons;

  @EJB
  private Events events;

  @EJB
  private Venues venues;

  @EJB
  private Halls halls;

  @EJB
  private ProductionVersions versions;

  @EJB
  private SeasonVenueProductions venueProductions;

  @EJB
  private Reservations reservations;

  @EJB
  private ReservationExtras extras;

  @EJB
  private ReservationTickets tickets;

  @EJB
  private SeasonPriceTemplates priceTemplates;

  @EJB
  private HallCapacities hallCapacities;

  @Inject
  private EventDTOConverter eventDTOConverter;

  @POST
  @Path("/validate")
  public Response validate(@PathParam("seasonId") Long seasonId, EventRequestDTO eventRequest) {

    Season season = seasons.get(seasonId);

    List<ValidationError> errors = validateRequest(season, eventRequest);

    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity(new ValidationErrorList(errors))
              .build();
    } else {
      return Response.ok().build();
    }
  }

  @POST
  @Path("/submit")
  public Response submit(@PathParam("seasonId") Long seasonId, EventRequestDTO eventRequest) {

    Season season = seasons.get(seasonId);
    List<ValidationError> errors = validateRequest(season, eventRequest);

    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity(new ValidationErrorList(errors))
              .build();
    }

    // create event
    Hall hall = halls.get(eventRequest.getHallId());
    ProductionVersion productionVersion = versions.get(eventRequest.getProductionVersionId());

    Event event = new Event();
    event.setSeason(season);
    event.setDate(eventRequest.getDate());
    event.setTime(eventRequest.getTime());
    event.setProduction(productionVersion);
    event.setHall(hall);
    event.setStatus(EventStatus.REQUESTED);
    event.setHidden(true);

    event = events.save(event);

    return Response.ok(eventDTOConverter.toDTO(event), MediaType.WILDCARD_TYPE).build();
  }

  private List<ValidationError> validateRequest(Season season, EventRequestDTO eventRequest) {

    List<ValidationError> errors = new LinkedList<>();

    if (season == null) {
      errors.add(new ValidationError("seasonId", "Spielzeit wurde nicht gefunden."));
      return errors;
    }

    if (eventRequest == null) {
      errors.add(new ValidationError(
              "eventRequest",
              "Reservierungsanfrage nicht forhanden oder falsch formatiert."));
      return errors;
    }

    // do basic validation for required fields
    if (eventRequest.getDate() == null) {
      errors.add(new ValidationError(
              "date",
              "Bitte geben Sie ein Datum an."));
    }

    if (eventRequest.getTime() == null) {
      errors.add(new ValidationError(
              "time",
              "Bitte geben Sie eine Uhrzeit an."));
    }

    if (eventRequest.getHallId() == null) {
      errors.add(new ValidationError(
              "hallId",
              "Bitte geben Sie einen Ort an."));
    }

    if (eventRequest.getProductionVersionId() == null) {
      errors.add(new ValidationError(
              "productionVersionId",
              "Bitte geben Sie eine Vorstellung an."));
    }

    if (!errors.isEmpty()) {
      return errors;
    }

    try (ValidatorFactory validationFactory = Validation.buildDefaultValidatorFactory()) {

      Validator validator = validationFactory.getValidator();

      errors.addAll(validator.validate(eventRequest).stream()
              .map(cv -> new ValidationError(cv.getPropertyPath().toString(), cv.getMessage()))
              .toList());

      if (!errors.isEmpty()) {
        return errors;
      }
    }

    // check plausibility
    if (eventRequest.getDate().isBefore(season.getStart())
            || eventRequest.getDate().isAfter(season.getEnd())) {
      errors.add(new ValidationError("date", String.format(
              "Es sind nur Buchungen zwischen dem %s und dem %s möglich.",
              season.getStart().format(DateTimeFormatter.ofLocalizedPattern("dd.MM.YYYY")),
              season.getEnd().format(DateTimeFormatter.ofLocalizedPattern("dd.MM.YYYY")))));
    }

    if (eventRequest.getTime().isBefore(LocalTime.of(8, 0))
            || eventRequest.getTime().isAfter(LocalTime.of(12, 0))) {

      // TODO make customizable
      errors.add(new ValidationError(
              "time",
              "Der Beginn muss zwischen 8:00 und 12:00 Uhr liegen."));
    }

    Hall hall = halls.get(eventRequest.getHallId());
    if (hall == null) {
      errors.add(new ValidationError(
              "hallId",
              "Der ausgewählte Saal existiert nicht."));
    }

    ProductionVersion productionVersion = versions.get(eventRequest.getProductionVersionId());
    if (productionVersion == null) {
      errors.add(new ValidationError(
              "productionVersionId",
              "Die ausgewählte Veranstaltung existiert nicht."));
    }

    if (hall != null && productionVersion != null
            && !venueProductions.exists(season, hall.getVenue(), productionVersion.getProduction())) {

      errors.add(new ValidationError(
              "productionVersionId",
              "Die ausgewählte Veranstaltung ist in der Spielstätte nicht verfügbar."));
    }

    return errors;
  }
}
