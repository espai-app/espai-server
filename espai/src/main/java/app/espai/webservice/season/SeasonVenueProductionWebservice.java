
package app.espai.webservice.season;

import app.espai.dao.Productions;
import app.espai.dao.SeasonVenueProductions;
import app.espai.dao.SeasonVenues;
import app.espai.dao.Seasons;
import app.espai.dao.Venues;
import app.espai.dto.ProductionDTOConverter;
import app.espai.dto.SeasonVenueDTOConverter;
import app.espai.filter.SeasonVenueProductionFilter;
import app.espai.model.Production;
import app.espai.model.Season;
import app.espai.model.SeasonVenueProduction;
import app.espai.model.Venue;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 *
 * @author rborowski
 */

@Path("/season/{seasonId}")
@Produces(MediaType.APPLICATION_JSON)
public class SeasonVenueProductionWebservice {

  @EJB
  private Seasons seasons;

  @EJB
  private Venues venues;

  @EJB
  private SeasonVenues seasonVenues;

  @EJB
  private Productions productions;

  @EJB
  private SeasonVenueProductions venueProductions;

  @Inject
  private ProductionDTOConverter productionConverter;

  @Inject
  private SeasonVenueDTOConverter seasonVenueConverter;

  @GET
  @Path("/venues/{venueSlug}/productions")
  public Response listProductions(@PathParam("seasonId") Long seasonId,
          @PathParam("venueSlug") String venueSlug) {

    Season season = seasons.get(seasonId);
    if (season == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Season not found,").build();
    }

    Venue venue = venues.getBySlug(venueSlug);
    if (venue == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Venue not found,").build();
    }

    SeasonVenueProductionFilter productionFilter = new SeasonVenueProductionFilter();
    productionFilter.setSeason(season);
    productionFilter.setVenue(venue);

    List<SeasonVenueProduction> productionList = venueProductions.list(productionFilter).getItems();

    return Response.ok(productionList.stream()
            .map(SeasonVenueProduction::getProduction)
            .map(p -> productionConverter.toDTO(p)).toList(), MediaType.APPLICATION_JSON)
            .build();
  }

  @GET
  @Path("/productions/{productionId}/venues")
  public Response listVenues(@PathParam("seasonId") Long seasonId,
          @PathParam("productionId") Long productionId) {

    Season season = seasons.get(seasonId);
    if (season == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Season not found,").build();
    }

    Production production = productions.get(productionId);
    if (production == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Production not found,").build();
    }

    SeasonVenueProductionFilter productionFilter = new SeasonVenueProductionFilter();
    productionFilter.setSeason(season);
    productionFilter.setProduction(production);

    List<SeasonVenueProduction> productionList = venueProductions.list(productionFilter).getItems();

    return Response.ok(productionList.stream()
            .map(SeasonVenueProduction::getVenue)
            .map(s -> seasonVenues.get(s.getId()))
            .map(p -> seasonVenueConverter.toDTO(p))
            .toList(), MediaType.APPLICATION_JSON)
            .build();
  }

}
