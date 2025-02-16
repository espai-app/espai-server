/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.model.Attachment;
import app.espai.sdk.model.AttachmentDTO;

/**
 *
 * @author rborowski
 */
public class AttachmentDTOConverter {
  
  public static AttachmentDTO of(Attachment attachment) {
    AttachmentDTO result = new AttachmentDTO();

    result.setId(attachment.getId());
    result.setMediaType(attachment.getMediaType().toString());
    result.setMimeType(attachment.getMimeType());
    result.setPosition(attachment.getPosition());
    result.setCaption(attachment.getCaption());
    result.setLocation(attachment.getLocation());
    result.setCopyright(attachment.getCopyright());

    return result;
  }
  
}
