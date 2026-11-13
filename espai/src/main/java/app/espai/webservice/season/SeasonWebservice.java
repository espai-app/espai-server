
package app.espai.webservice.season;

import app.espai.dao.Seasons;
import app.espai.dto.SeasonDTOConverter;
import app.espai.model.Season;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author rborowski
 */
@Path("/season/{seasonId}")
@Produces(MediaType.APPLICATION_JSON)
public class SeasonWebservice {

  @EJB
  private Seasons seasons;

  @Inject
  private SeasonDTOConverter seasonConverter;

  @GET
  public Response get(@PathParam("seasonId") Long seasonId) {

    Season season = seasons.get(seasonId);
    if (season == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Season not found.").build();
    }

    return Response.ok(seasonConverter.toDTO(season), MediaType.APPLICATION_JSON).build();
  }
}
