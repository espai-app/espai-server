/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.model.Attachment;
import app.espai.model.AttachmentType;
import app.espai.sdk.model.AttachmentDTO;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class AttachmentDTOConverter {

  public AttachmentDTO toDTO(Attachment attachment) {

    if (attachment == null) {
      return null;
    }

    AttachmentDTO result = new AttachmentDTO();

    result.setId(attachment.getId());
    result.setMediaType(attachment.getMediaType().toString());
    result.setMimeType(attachment.getMimeType());
    result.setCategory(attachment.getCategory());
    result.setPosition(attachment.getPosition());
    result.setCaption(attachment.getCaption());
    result.setLocation(attachment.getLocation());
    result.setCopyright(attachment.getCopyright());

    return result;
  }

  public Attachment fromDTO(AttachmentDTO attachment) {
    if (attachment == null) {
      return null;
    }

    Attachment result = new Attachment();

    result.setId(attachment.getId());
    result.setMediaType(AttachmentType.valueOf(attachment.getMediaType()));
    result.setMimeType(attachment.getMimeType());
    result.setCategory(attachment.getCategory());
    result.setPosition(attachment.getPosition());
    result.setCaption(attachment.getCaption());
    result.setLocation(attachment.getLocation());
    result.setCopyright(attachment.getCopyright());

    return result;
  }

}
