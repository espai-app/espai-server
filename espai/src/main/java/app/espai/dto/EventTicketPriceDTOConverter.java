/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.model.EventTicketPrice;
import app.espai.sdk.model.EventTicketPriceDTO;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class EventTicketPriceDTOConverter {

  public EventTicketPriceDTO toDTO(EventTicketPrice price) {

    if (price == null) {
      return null;
    }

    EventTicketPriceDTO result = new EventTicketPriceDTO();
    result.setId(price.getId());
    result.setSeatCategory(price.getSeatCategory().getName());
    result.setPriceCategory(price.getPriceCategory().getName());
    result.setDescription(price.getPriceCategory().getDescription());
    result.setPrice(price.getPrice().getAmount().multiply(BigDecimal.valueOf(100)).intValue());
    result.setCurrency(price.getPrice().getCurrency());
    return result;
  }

}
