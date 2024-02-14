package app.espai.webservice;

import app.espai.dao.SeasonVenues;
import app.espai.dao.Seasons;
import app.espai.dao.Venues;
import app.espai.filter.SeasonVenueFilter;
import app.espai.model.Season;
import app.espai.model.Venue;
import jakarta.ejb.EJB;
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
@Path("/season/{seasonId}/venues")
@Produces(MediaType.APPLICATION_JSON)
public class VenueWebservice {

  @EJB
  private Seasons seasons;

  @EJB
  private Venues venues;

  @EJB
  private SeasonVenues seasonVenues;

  @GET
  @Path("{venueId}")
  public Response get(@PathParam("seasonId") Long seasonId, @PathParam("venueId") Long venueId) {

    Venue venue = venues.get(venueId);
    Season season = seasons.get(seasonId);

    SeasonVenueFilter filter = new SeasonVenueFilter();
    filter.setSeason(season);
    filter.setVenue(venue);

    if (seasonVenues.list(filter).getTotalNumberOfResults() == 0) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(VenueDTO.of(venue), MediaType.APPLICATION_JSON).build();
  }

  @GET
  public List<VenueDTO> list(@PathParam("seasonId") Long seasonId) {

    Season season = seasons.get(seasonId);

    List<Venue> seasonVenueList = seasonVenues.getVenues(season);
    seasonVenueList.sort(Venues.DEFAULT_ORDER);

    List<VenueDTO> venueList = seasonVenueList.stream().map(v -> VenueDTO.of(v)).toList();

    return venueList;
  }

}
