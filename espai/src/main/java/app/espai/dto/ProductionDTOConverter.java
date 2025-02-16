/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.model.Attachment;
import app.espai.model.Production;
import app.espai.model.ProductionTag;
import app.espai.sdk.model.AttachmentDTO;
import app.espai.sdk.model.ProductionDTO;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rborowski
 */
public class ProductionDTOConverter {
  
  public static ProductionDTO of(Production production, List<Attachment> attachments) {
    ProductionDTO result = new ProductionDTO();
    result.setId(production.getId());
    result.setTitle(production.getTitle());
    result.setDescription(production.getDescription());
    result.setDurationInMinutes(production.getDurationInMinutes());
    result.setFromAge(production.getFromAge());
    result.setToAge(production.getToAge());
    result.setRating(production.getRating());

    if (attachments != null) {
      Map<String, List<AttachmentDTO>> attachmentDTOs = new LinkedHashMap<>();
      for (Attachment a : attachments) {
        if (!attachmentDTOs.containsKey(a.getCategory())) {
          attachmentDTOs.put(a.getCategory(), new LinkedList<>());
        }
        attachmentDTOs.get(a.getCategory()).add(AttachmentDTOConverter.of(a));
      }
      result.setAttachments(attachmentDTOs);
    }

    return result;
  }

  public static ProductionDTO of(Production production,
          List<Attachment> attachments,
          List<ProductionTag> productionTags) {

    ProductionDTO result = ProductionDTOConverter.of(production, attachments);

    Map<String, List<String>> tags = new LinkedHashMap<>();
    for (ProductionTag t : productionTags) {
      if (!tags.containsKey(t.getCategory())) {
        tags.put(t.getCategory(), new LinkedList<>());
      }
      tags.get(t.getCategory()).add(t.getValue());
    }
    result.setTags(tags);

    return result;
  }
  
}
