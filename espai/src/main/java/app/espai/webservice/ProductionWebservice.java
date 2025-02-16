package app.espai.webservice;

import app.espai.dao.Attachments;
import app.espai.dao.Events;
import app.espai.dao.ProductionTags;
import app.espai.dao.Productions;
import app.espai.dao.SeasonProductions;
import app.espai.dao.Seasons;
import app.espai.dto.MovieDTOConverter;
import app.espai.dto.ProductionDTOConverter;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.ProductionTagFilter;
import app.espai.filter.SeasonProductionFilter;
import app.espai.model.Attachment;
import app.espai.model.Movie;
import app.espai.model.Production;
import app.espai.model.ProductionTag;
import app.espai.model.Season;
import app.espai.sdk.model.ProductionDTO;
import jakarta.ejb.EJB;
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

/**
 *
 * @author rborowski
 */
@Path("season/{seasonId}/productions")
@Produces(MediaType.APPLICATION_JSON)
public class ProductionWebservice {

  @EJB
  private Seasons seasons;

  @EJB
  private Productions productions;

  @EJB
  private Events events;

  @EJB
  private ProductionTags productionTags;

  @EJB
  private Attachments attachments;

  @EJB
  private SeasonProductions seasonProductions;

  @GET
  @Path("{productionId}")
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

    return Response.ok(toDTO(production)).build();
  }

  @GET
  public List<ProductionDTO> list(
          @PathParam("seasonId") Long seasonId,
          @QueryParam("type") String type,
          @QueryParam("since") String sinceParam) {

    Season season = seasons.get(seasonId);

    LocalDate since = LocalDate.now();
    if (sinceParam != null && sinceParam.matches("\\d{4}-\\d{2}-\\d{2}")) {
      since = LocalDate.parse(sinceParam, DateTimeFormatter.ISO_DATE);
    }

    List<Production> productionList = events.listDistinctProductions(season, since);

    if (type != null && !type.isBlank()) {
      productionList = productionList.stream().filter(
              p -> p.getClass().getSimpleName().equals(type)).toList();
    }

    return productionList.stream().map(p -> toDTO(p)).toList();
  }

  private ProductionDTO toDTO(Production production) {

    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityType(production.getClass().getSimpleName());
    attachmentFilter.setEntityId(production.getId());
    List<Attachment> attachmentList = attachments.list(attachmentFilter).getItems();

    ProductionTagFilter tagFilter = new ProductionTagFilter();
    tagFilter.setProduction(production);
    List<ProductionTag> tagList = productionTags.list(tagFilter).getItems();

    if (production instanceof Movie) {
      return MovieDTOConverter.of((Movie) production, attachmentList, tagList);
    }
    return ProductionDTOConverter.of(production, attachmentList, tagList);
  }

}
