/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.model.ReservationForm;
import app.espai.model.ReservationFormField;
import app.espai.sdk.model.ReservationFormDTO;
import app.espai.sdk.model.ReservationFormFieldDTO;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rborowski
 */
public class ReservationFormDTOConverter {
  
  public static ReservationFormDTO convert(ReservationForm form, List<ReservationFormField> fields) {
    
    ReservationFormDTO result = new ReservationFormDTO();
    result.setName(form.getName());
    result.setTermsConditionsUrl(form.getTermsConditionsUrl());
    result.setPrivacyUrl(form.getPrivacyUrl());
    
    List<ReservationFormFieldDTO> fieldDTOs = new LinkedList<>();
    for (ReservationFormField f : fields) {
      ReservationFormFieldDTO fdto = new ReservationFormFieldDTO();
      fdto.setLabel(f.getLabel());
      fdto.setName(f.getName());
      fdto.setType(f.getFieldType().toString());
      if (f.getPresets() != null && !f.getPresets().isBlank()) {
        fdto.setPresets(List.of(f.getPresets().split("\n")).stream()
                .map(p -> p.trim())
                .filter(p -> !p.isBlank())
                .toList());
      }
      fdto.setRequired(f.isRequired());
      fieldDTOs.add(fdto);
    }
    result.setFields(fieldDTOs);
    return result;
  }
}
