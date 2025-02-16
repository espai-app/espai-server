/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.model.EventSerial;
import app.espai.sdk.model.EventSerialDTO;

/**
 *
 * @author rborowski
 */
public class EventSerialDTOConverter {
  
  public static EventSerialDTO of(EventSerial serial) {
    EventSerialDTO result = new EventSerialDTO();
    result.setId(serial.getId());
    result.setName(serial.getName());
    return result;
  }
  
}
