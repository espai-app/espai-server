package app.espai.webservice;

import app.espai.dao.Attachments;
import app.espai.filter.AttachmentFilter;
import app.espai.model.Attachment;
import static app.espai.model.AttachmentType.IMAGE;
import static app.espai.model.AttachmentType.URL;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;
import rocks.xprs.util.Streams;

/**
 *
 * @author rborowski
 */
@Stateless
@Path("attachments")
public class AttachmentsWebservice {

  @EJB
  private Attachments attachments;

  @GET
  @Path("/{attachmentId}/datastream")
  public Response getAttachment(@PathParam("attachmentId") long attachmentId) throws IOException {

    Attachment attachment = attachments.get(attachmentId);

    switch (attachment.getMediaType()) {
      case URL:
        return Response.seeOther(URI.create(attachment.getLocation())).build();
      case IMAGE:
        Response res =  Response
                .ok(attachments.getDataStream(attachment), attachment.getMimeType())
                .build();
        return res;
      default:
        return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Path("first/original/{entityType}/{entityId}/{category}/")
  public Response getFirstOriginal(
          @PathParam("entityType") String entityType,
          @PathParam("entityId") long entityId,
          @PathParam("category") String category) throws IOException {

    Attachment attachment = getFirstAttachment(entityType, entityId, category);

    switch (attachment.getMediaType()) {
      case URL:
        return Response.seeOther(URI.create(attachment.getLocation())).build();
      case IMAGE:
        return Response.ok(new ResponseStream(
                attachments.getDataStream(attachment)), attachment.getMimeType())
                .build();
      default:
        return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Path("first/fit/{entityType}/{entityId}/{category}/{width}/{height}")
  public Response getFirstFit(
          @PathParam("entityType") String entityType,
          @PathParam("entityId") long entityId,
          @PathParam("category") String category,
          @PathParam("width") int width,
          @PathParam("height") int height)
          throws IOException {

    Attachment attachment = getFirstAttachment(entityType, entityId, category);

    switch (attachment.getMediaType()) {
      case URL:
        return Response.seeOther(URI.create(attachment.getLocation())).build();
      case IMAGE:
        return Response.ok(new ResponseStream(
                attachments.getScaledImageStream(attachment, width, height)),
                attachment.getMimeType())
                .build();
      default:
        return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Path("first/cropped/{entityType}/{entityId}/{category}/{width}/{height}")
  public Response getFirstCropped(
          @PathParam("entityType") String entityType,
          @PathParam("entityId") long entityId,
          @PathParam("category") String category,
          @PathParam("width") int width,
          @PathParam("height") int height)
          throws IOException {

    Attachment attachment = getFirstAttachment(entityType, entityId, category);

    switch (attachment.getMediaType()) {
      case URL:
        return Response.seeOther(URI.create(attachment.getLocation())).build();
      case IMAGE:
        return Response.ok(new ResponseStream(
                attachments.getCroppedImageStream(attachment, width, height)),
                attachment.getMimeType())
                .build();
      default:
        return Response.status(Response.Status.NOT_FOUND).build();
    }
  }


  private Attachment getFirstAttachment(String entityType, Long entityId, String category) {
    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityType(entityType);
    attachmentFilter.setEntityId(entityId);
    attachmentFilter.setCategory(category);
    attachmentFilter.setOrderBy("position");
    attachmentFilter.setOrder(PageableFilter.Order.ASC);

    List<Attachment> attachmentList = attachments.list(attachmentFilter).getItems();
    if (attachmentList.isEmpty()) {
      return null;
    }

    return attachmentList.get(0);
  }

  private class ResponseStream implements StreamingOutput {

    private final InputStream inputStream;

    public ResponseStream(InputStream inputStream) {
      this.inputStream = inputStream;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException, WebApplicationException {
      try (inputStream;
              outputStream) {

        Streams.copy(inputStream, outputStream);
      }
    }
  }

}
