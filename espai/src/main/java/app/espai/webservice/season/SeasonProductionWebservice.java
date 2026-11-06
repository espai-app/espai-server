package app.espai.webservice.season;

import app.espai.dao.Events;
import app.espai.dao.ProductionTypes;
import app.espai.dao.Productions;
import app.espai.dao.SeasonProductions;
import app.espai.dao.Seasons;
import app.espai.dto.ProductionDTOConverter;
import app.espai.filter.SeasonProductionFilter;
import app.espai.model.Production;
import app.espai.model.ProductionType;
import app.espai.model.Season;
import app.espai.sdk.model.ProductionDTO;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import app.espai.webservice.auth.RequireManager;

/**
 *
 * @author rborowski
 */
@Path("season/{seasonId}/productions")
@Produces(MediaType.APPLICATION_JSON)
public class SeasonProductionWebservice {

  @EJB
  private Seasons seasons;

  @EJB
  private Productions productions;

  @EJB
  private ProductionTypes productionTypes;

  @EJB
  private Events events;

  @EJB
  private SeasonProductions seasonProductions;

  @Inject
  private ProductionDTOConverter productionConverter;

  @GET
  @Path("{productionId}")
  @RequireManager
  public Response get(
          @PathParam("seasonId") long seasonId,
          @PathParam("productionId") long productionId) {

    Season season = seasons.get(seasonId);
    Production production = productions.get(productionId);

    SeasonProductionFilter filter = new SeasonProductionFilter();
    filter.setSeason(season);
    filter.setProduction(production);

    if (seasonProductions.list(filter).getTotalNumberOfResults() == 0) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(productionConverter.toDTO(production)).build();
  }

  @GET
  public List<ProductionDTO> list(
          @PathParam("seasonId") Long seasonId,
          @QueryParam("productionTypeId") Long productionTypeId,
          @QueryParam("since") String sinceParam) {

    Season season = seasons.get(seasonId);

    LocalDate since = LocalDate.now();
    if (sinceParam != null && sinceParam.matches("\\d{4}-\\d{2}-\\d{2}")) {
      since = LocalDate.parse(sinceParam, DateTimeFormatter.ISO_DATE);
    }

    ProductionType productionType = productionTypes.get(productionTypeId);

    List<Production> productionList = events.listDistinctProductions(season, productionType, since);

    return productionList.stream().map(p -> productionConverter.toDTO(p)).toList();
  }
}
