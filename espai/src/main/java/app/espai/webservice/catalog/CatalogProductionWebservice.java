
package app.espai.webservice.catalog;

import app.espai.dao.Agencies;
import app.espai.dao.Attachments;
import app.espai.dao.LicenseConditions;
import app.espai.dao.ProductionMetaValues;
import app.espai.dao.ProductionVersions;
import app.espai.dao.Productions;
import app.espai.dto.ProductionDTOConverter;
import app.espai.dto.ProductionDTOConverter.ProductionPackage;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.ProductionFilter;
import app.espai.filter.ProductionMetaValueFilter;
import app.espai.model.Attachment;
import app.espai.model.AttachmentType;
import app.espai.model.LicenseCondition;
import app.espai.model.Production;
import app.espai.model.ProductionMetaValue;
import app.espai.model.ProductionVersion;
import app.espai.sdk.model.ListResult;
import app.espai.sdk.model.ProductionDTO;
import app.espai.sdk.model.ValidationError;
import app.espai.webservice.auth.RequireManager;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import rocks.xprs.runtime.db.PageableFilter;
import rocks.xprs.runtime.db.PageableList;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
@Path("/productions/")
public class CatalogProductionWebservice {

  @EJB
  private Productions productions;

  @EJB
  private ProductionMetaValues metaValues;

  @EJB
  private ProductionVersions versions;

  @EJB
  private Attachments attachments;

  @EJB
  private LicenseConditions licenses;

  @EJB
  private Agencies agencies;

  @Inject
  private ProductionDTOConverter productionConverter;

  @GET
  @Path("/{id}")
  @Operation(summary = "Get production by ID")
  @APIResponse(
          responseCode = "200",
          content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProductionDTO.class)))
  @APIResponse(
          responseCode = "404",
          description = "Production not found.")
  public Response get(@PathParam("id") Long id) {
    Production result = productions.get(id);

    if (result == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    return Response.ok(productionConverter.toDTO(result), MediaType.APPLICATION_JSON).build();
  }

  @GET
  @RequireManager
  @Operation(summary = "Get a list of productions.")
  @APIResponse(
          responseCode = "200",
          content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ListResult.class)))
  public Response list(
          @Parameter(description = "Offset") @QueryParam("offset") Integer offset,
          @Parameter(description = "Max. number of results") @QueryParam("limit") Integer limit) {

    if (offset == null || offset < 0) {
      offset = 0;
    }

    if (limit == null || limit < 1) {
      limit = 100;
    }

    ProductionFilter filter = new ProductionFilter();
    filter.setOrderBy("id");
    filter.setOrder(PageableFilter.Order.ASC);
    filter.setOffset(offset);
    filter.setLimit(limit);

    PageableList<Production> productionList = productions.list(filter);

    ListResult<ProductionDTO> result = new ListResult<>();
    result.setItems(productionList.getItems().stream()
            .map(i -> productionConverter.toDTO(i))
            .toList());
    result.setTotal(productionList.getTotalNumberOfResults());

    return Response.ok(result, MediaType.APPLICATION_JSON).build();
  }

  @POST
  @Transactional
  @RequireManager
  @Operation(summary = "Create a new production.")
  @APIResponse(responseCode = "201", description = "The production was created successfully.")
  @APIResponse(responseCode = "400", description = "The are validation errors.")
  public Response create(@Context UriInfo uriInfo,
          @RequestBody(
                  description = "The production object.",
                  required = true,
                  content=@Content(schema=@Schema(implementation = ProductionDTO.class)))
                  ProductionDTO productionDTO) {

    return createOrUpdate(productionDTO, uriInfo, true);
  }

  @PUT
  @Path("/{id}")
  @Transactional
  @RequireManager
  @Operation(summary = "Update a production.")
  @APIResponse(responseCode = "202", description = "The production was updated successfully.")
  @APIResponse(responseCode = "400", description = "The are validation errors.")
  public Response update(@Context UriInfo uriInfo,
          @Parameter(description = "The ID of the production to update.") @PathParam("id") Long id,
          @RequestBody(
                  description = "The production object.",
                  required = true,
                  content=@Content(schema=@Schema(implementation = ProductionDTO.class)))
                  ProductionDTO productionDTO) {

    productionDTO.setId(id);
    return createOrUpdate(productionDTO, uriInfo, false);
  }

  @DELETE
  @Transactional
  @RequireManager
  @Path("/{id}")
  @Operation(summary = "Delete a production.")
  @APIResponse(responseCode = "202", description = "The production was updated successfully.")
  @APIResponse(responseCode = "404", description = "The production was not found.")
  public Response delete(
          @Parameter(description = "The ID of the production to be deleted.")
          @PathParam("id")
                  Long id) {
    Production result = productions.get(id);

    if (result == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    productions.delete(result);

    return Response.accepted().build();
  }

  private Response createOrUpdate(ProductionDTO productionDTO, UriInfo uriInfo, boolean isCreate) {
    ProductionPackage newProduction = productionConverter.fromDTO(productionDTO);

    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    // validate user input
    List<ValidationError> constraintViolations = new LinkedList<>();

    if (newProduction == null || newProduction.getProduction() == null) {
      constraintViolations.add(new ValidationError("production", "Production must not be null."));
    } else {
      constraintViolations.addAll(validator.validate(newProduction.getProduction()).stream()
              .map(e -> new ValidationError(
                      "production." + e.getPropertyPath().toString(),
                      e.getMessage()))
              .toList());
      constraintViolations.addAll(validator.validate(newProduction.getMetaValueList()).stream()
              .map(e -> new ValidationError(
                      "metaValues." + e.getPropertyPath().toString(),
                      e.getMessage()))
              .toList());
      constraintViolations.addAll(validator.validate(newProduction.getVersionList()).stream()
              .map(e -> new ValidationError(
                      "versionList." + e.getPropertyPath().toString(),
                      e.getMessage()))
              .toList());

      constraintViolations.addAll(validator.validate(newProduction.getLicenseList()).stream()
              .map(e -> new ValidationError(
                      "licenseList." + e.getPropertyPath().toString(),
                      e.getMessage()))
              .toList());
    }

    if (!constraintViolations.isEmpty()) {
      return Response
              .status(Response.Status.BAD_REQUEST)
              .entity(constraintViolations)
              .type(MediaType.APPLICATION_JSON)
              .build();
    }

    // save production
    if (isCreate) {
      newProduction.getProduction().setId(null);
    }

    Production p = productions.save(newProduction.getProduction());

    // Sets and replaces existing meta values, Leaves data untouched, that is not mentioned.
    if (newProduction.getMetaValueList() != null) {
      ProductionMetaValueFilter metaValueFilter = new ProductionMetaValueFilter();
      metaValueFilter.setProduction(p);
      Map<String, ProductionMetaValue> existingMetaValues = metaValues.list(metaValueFilter)
              .getItems()
              .stream()
              .collect(Collectors.toMap(v -> v.getProductionMetaField().getName(), v -> v));

      for(ProductionMetaValue v : newProduction.getMetaValueList()) {

        ProductionMetaValue existingValue = existingMetaValues.get(
                v.getProductionMetaField().getName());

        if (existingValue != null) {
          existingValue.setValue(v.getValue());
          metaValues.save(existingValue);
        } else {
          v.setId(null);
          v.setProduction(p);
          metaValues.save(v);
        }
      }
    }

    // add new versions (doesn't delete versions that are not in teh list)
    if (newProduction.getVersionList() != null) {
      for(ProductionVersion v : newProduction.getVersionList()) {

        if (isCreate) {
          v.setId(null);
        }

        v.setProduction(p);
        metaValues.save(v);
      }
    }

    if (newProduction.getAttachmentList() != null) {
      AttachmentFilter attachmentFilter = new AttachmentFilter();
      attachmentFilter.setEntityType(Production.class.getSimpleName());
      attachmentFilter.setEntityId(p.getId());

      List<Attachment> existingAttachments = attachments.list(attachmentFilter).getItems();
      if (existingAttachments.isEmpty() && newProduction.getAttachmentList() != null ) {
        HttpClient httpClient = HttpClient.newHttpClient();

        for (Attachment a : newProduction.getAttachmentList()) {

          // create attachments (if none)
          a.setEntityType(Production.class.getSimpleName());
          a.setEntityId(p.getId());
          a = attachments.save(a);

          // download images to disk
          if (a.getMediaType() == AttachmentType.IMAGE
                  && a.getLocation() != null
                  && a.getLocation().matches("^https?://.*")) {
            try {
              HttpRequest imageRequest = HttpRequest.newBuilder(URI.create(a.getLocation()))
                      .GET().build();

              HttpResponse<InputStream> imageResponse = httpClient.send(
                      imageRequest, HttpResponse.BodyHandlers.ofInputStream());

              attachments.setDataStream(a, imageResponse.body());
            } catch (IOException | InterruptedException ex) {
              Logger.getLogger(CatalogProductionWebservice.class.getName()).log(
                      Level.SEVERE,
                      "Image for %s could not be loaded.".formatted(a.getLocation()),
                      ex);
            }
          }
        }
      }
    }

    if (newProduction.getLicenseList() != null) {
      for(LicenseCondition l : newProduction.getLicenseList()) {

        if (isCreate) {
          l.setId(null);
        }

        if (l.getAgency() != null && l.getAgency().getId() == null) {
          agencies.save(l.getAgency());
        }

        l.setProduction(p);
        licenses.save(l);
      }
    }

    if (isCreate) {
      return Response.created(uriInfo.getAbsolutePath().resolve(String.valueOf(p.getId())))
              .build();
    } else {
      return Response.accepted().build();
    }
  }

}
