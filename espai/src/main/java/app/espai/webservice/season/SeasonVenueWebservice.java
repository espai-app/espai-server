package app.espai.webservice.season;

import app.espai.dao.Halls;
import app.espai.dao.SeasonVenueProductions;
import app.espai.dao.SeasonVenues;
import app.espai.dao.Seasons;
import app.espai.dao.Venues;
import app.espai.dto.HallDTOConverter;
import app.espai.dto.ProductionDTOConverter;
import app.espai.dto.SeasonVenueDTOConverter;
import app.espai.filter.HallFilter;
import app.espai.filter.SeasonVenueFilter;
import app.espai.filter.SeasonVenueProductionFilter;
import app.espai.model.Season;
import app.espai.model.SeasonVenue;
import app.espai.model.Venue;
import app.espai.sdk.model.HallDTO;
import app.espai.sdk.model.ProductionDTO;
import app.espai.sdk.model.SeasonVenueDTO;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import rocks.xprs.runtime.db.PageableList;

/**
 *
 * @author rborowski
 */
@Path("/season/{seasonId}/venues")
@Produces(MediaType.APPLICATION_JSON)
public class SeasonVenueWebservice {

  @EJB
  private Seasons seasons;

  @EJB
  private Venues venues;

  @EJB
  private Halls halls;

  @EJB
  private SeasonVenues seasonVenues;

  @EJB
  private SeasonVenueProductions seasonProductions;

  @Inject
  private SeasonVenueDTOConverter seasonVenueConverter;

  @Inject
  private ProductionDTOConverter productionConverter;

  @Inject
  private HallDTOConverter hallConverter;

  @GET
  @Path("{venueSlug}")
  public Response get(@PathParam("seasonId") Long seasonId, @PathParam("venueSlug") String slug) {

    Season season = seasons.get(seasonId);
    if (season == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Season not found.").build();
    }

    Venue venue = venues.getBySlug(slug);
    if (venue == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Venue not found.").build();
    }

    SeasonVenueFilter filter = new SeasonVenueFilter();
    filter.setSeason(season);
    filter.setVenue(venue);

    PageableList<SeasonVenue> seasonVenueList = seasonVenues.list(filter);
    if (seasonVenueList.getTotalNumberOfResults() == 0) {
      return Response.status(Response.Status.NOT_FOUND).entity("Venue not found in season.").build();
    }

    return Response
            .ok(seasonVenueConverter.toDTO(seasonVenueList.getItems().get(0)),
                    MediaType.APPLICATION_JSON)
            .build();
  }

  @GET
  @Path("{venueSlug}/halls")
  public Response listHalls(@PathParam("seasonId") Long seasonId,
          @PathParam("venueSlug") String slug) {

    Season season = seasons.get(seasonId);
    if (season == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Season not found.").build();
    }

    Venue venue = venues.getBySlug(slug);
    if (venue == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Venue not found.").build();
    }

    SeasonVenue seasonVenue = seasonVenues.getVenue(season, venue);
    if (seasonVenue == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Venue not found.").build();
    }

    HallFilter hallFilter = new HallFilter();
    hallFilter.setVenue(venue);
    List<HallDTO> hallDTOList = halls.list(hallFilter).getItems()
            .stream()
            .map(h -> hallConverter.toDTO(h))
            .toList();

    return Response.ok(hallDTOList, MediaType.APPLICATION_JSON).build();
  }

  @GET
  @Path("{venueSlug}/productions")
  public Response listProductions(@PathParam("seasonId") Long seasonId,
          @PathParam("venueSlug") String slug) {

    Season season = seasons.get(seasonId);
    if (season == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Season not found.").build();
    }

    Venue venue = venues.getBySlug(slug);
    if (venue == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Venue not found.").build();
    }

    SeasonVenueProductionFilter productionFilter = new SeasonVenueProductionFilter();
    productionFilter.setSeason(season);
    productionFilter.setVenue(venue);

    List<ProductionDTO> productionList = seasonProductions.list(productionFilter).getItems()
            .stream()
            .map(vp -> productionConverter.toShortDTO(vp.getProduction()))
            .toList();

    return Response.ok(productionList, MediaType.APPLICATION_JSON).build();
  }

  @GET
  public List<SeasonVenueDTO> list(@PathParam("seasonId") Long seasonId) {

    Season season = seasons.get(seasonId);

    SeasonVenueFilter filter = new SeasonVenueFilter();
    filter.setSeason(season);

    List<SeasonVenue> seasonVenueList = seasonVenues.list(filter).getItems();
    seasonVenueList.sort(SeasonVenue.DEFAULT_ORDER);

    List<SeasonVenueDTO> venueList = seasonVenueList
            .stream()
            .map(v -> seasonVenueConverter.toDTO(v))
            .toList();

    return venueList;
  }

}
