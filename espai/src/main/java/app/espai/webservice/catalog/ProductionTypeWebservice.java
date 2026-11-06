package app.espai.webservice.catalog;

import app.espai.dao.ProductionTypes;
import app.espai.dto.ProductionTypeDTOConverter;
import app.espai.model.ProductionType;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
@Path("/productionTypes")
public class ProductionTypeWebservice {

  @EJB
  private ProductionTypes productionTypes;

  @Inject
  private ProductionTypeDTOConverter typeConverter;

  @GET
  @Path("/{id}")
  public Response get(@PathParam("id") Long id) {

    ProductionType result = productionTypes.get(id);

    if (result == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(typeConverter.toDTO(result), MediaType.APPLICATION_JSON).build();
  }

  @GET
  public Response list() {

    List<? extends ProductionType> result = productionTypes.list().getItems();
    return Response.ok(
            result.stream().map(r -> typeConverter.toDTO(r)).toList(),
            MediaType.APPLICATION_JSON)
            .build();
  }

}
