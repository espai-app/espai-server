/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.model.Attachment;
import app.espai.model.Event;
import app.espai.model.EventTicketPrice;
import app.espai.model.SeatCategory;
import app.espai.sdk.model.EventDTO;
import app.espai.sdk.model.EventTicketPriceDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author rborowski
 */
public class EventDTOConverter {
  
  public static EventDTO of(Event event, List<Attachment> attachments,
          List<EventTicketPrice> prices, List<SeatCategory> seatCategories) {

    EventDTO result = new EventDTO();
    result.setId(event.getId());
    result.setDate(event.getDate());
    result.setTime(event.getTime());
    result.setTitle(event.getProduction().getProduction().getTitle());
    result.setVersion(event.getProduction().getVersionName());
    result.setProduction(ProductionDTOConverter.of(event.getProduction().getProduction(), attachments));
    result.setVenue(VenueDTOConverter.of(event.getHall().getVenue()));
    result.setHost(event.getHost() != null ? PresenterDTOConverter.of(event.getHost()) : null);
    result.setCoHost(event.getCoHost() != null ? PresenterDTOConverter.of(event.getCoHost()) : null);
    result.setTicketLimit(event.getTicketLimit() == null ? event.getHall().getCapacity() : event.getTicketLimit());
    result.setMandatory(event.isMandatory());
    result.setReservable(event.isReservable());

    if (prices != null) {
      List<EventTicketPriceDTO> priceDTOs = prices.stream()
              .map(p -> EventTicketPriceDTOConverter.of(p))
              .toList();
      result.setPrices(priceDTOs);
    }

    if (seatCategories != null) {
      result.setAvailableTickets(seatCategories.stream()
              .collect(
                      Collectors.toMap(SeatCategory::getName,
                              SeatCategory::getSeatsAvailable)));
    }

    return result;
  }
  
}
