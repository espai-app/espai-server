package app.espai.views.attachments;

import app.espai.dao.Attachments;
import app.espai.model.Attachment;
import app.espai.model.AttachmentType;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class AttachmentEditorView implements Serializable {

  @EJB
  private Attachments attachments;

  private Attachment attachment;

  private UploadedFile uploadedFile;

  @PostConstruct
  public void init() {

    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String attachmentIdParam = econtext.getRequestParameterMap().get("attachmentId");
    if (attachmentIdParam != null && attachmentIdParam.matches("\\d+")) {
      attachment = attachments.get(Long.parseLong(attachmentIdParam));
    } else {

      String entityIdParam = econtext.getRequestParameterMap().get("entityId");
      String entityTypeParam = econtext.getRequestParameterMap().get("entityType");
      String mediaTypeParam = econtext.getRequestParameterMap().get("mediaType");
      String categoryParam = econtext.getRequestParameterMap().get("category");

      if (entityIdParam == null || !entityIdParam.matches("\\d+") || entityTypeParam == null
              || mediaTypeParam == null) {

        throw new RuntimeException("entityId, entityType or mediaType is missing.");
      }

      attachment = new Attachment();
      attachment.setEntityId(Long.parseLong(entityIdParam));
      attachment.setEntityType(entityTypeParam);
      attachment.setMediaType(AttachmentType.valueOf(mediaTypeParam));
      attachment.setCategory(categoryParam);
    }
  }

  public void save() {
    try {
      if (uploadedFile != null) {
        attachment.setMimeType(uploadedFile.getContentType());
      }

      attachment = attachments.save(attachment);

      if (uploadedFile != null) {
        attachments.setDataStream(attachment, uploadedFile.getInputStream());
      }
    } catch (IOException ex) {
      Logger.getLogger(AttachmentEditorView.class.getName()).log(
              Level.SEVERE, "Could not save attachment data stream to file.", ex);
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
              "Die Datei konnte nicht gespeichert werden."));
    }

    PrimeFaces.current().dialog().closeDynamic(attachment);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the attachment
   */
  public Attachment getAttachment() {
    return attachment;
  }

  /**
   * @param attachment the attachment to set
   */
  public void setAttachment(Attachment attachment) {
    this.attachment = attachment;
  }

  /**
   * @return the uploadedFile
   */
  public UploadedFile getUploadedFile() {
    return uploadedFile;
  }

  /**
   * @param uploadedFile the uploadedFile to set
   */
  public void setUploadedFile(UploadedFile uploadedFile) {
    this.uploadedFile = uploadedFile;
  }
  //</editor-fold>

}
