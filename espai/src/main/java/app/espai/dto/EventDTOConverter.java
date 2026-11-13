/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.businesslogic.CapacityCalculator;
import app.espai.dao.EventTicketPrices;
import app.espai.dao.Events;
import app.espai.filter.EventFilter;
import app.espai.filter.EventTicketPriceFilter;
import app.espai.model.Event;
import app.espai.model.EventStatus;
import app.espai.model.EventTicketPrice;
import app.espai.model.SeatCategory;
import app.espai.sdk.model.EventDTO;
import app.espai.sdk.model.EventTicketPriceDTO;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class EventDTOConverter {

  @EJB
  private Events events;

  @EJB
  private EventTicketPrices prices;

  @Inject
  private SeasonDTOConverter seasonConverter;

  @Inject
  private EventTicketPriceDTOConverter priceConverter;

  @Inject
  private ProductionDTOConverter productionConverter;

  @Inject
  private ProductionVersionDTOConverter productionVersionConverter;

  @Inject
  private VenueDTOConverter venueConverter;

  @Inject
  private HallDTOConverter hallConverter;

  @Inject
  private EventSerialDTOConverter serialConverter;

  @Inject
  private CapacityCalculator capacityCalculator;

  public EventDTO toDTO(Event event) {

    if (event == null) {
      return null;
    }

    EventDTO result = new EventDTO();
    result.setId(event.getId());
    result.setSeason(seasonConverter.toDTO(event.getSeason()));
    result.setDate(event.getDate());
    result.setTime(event.getTime());
    result.setTitle(event.getProduction().getProduction().getTitle());
    result.setVersion(productionVersionConverter.toDTO(event.getProduction()));
    result.setProduction(productionConverter.toDTO(event.getProduction().getProduction()));
    result.setVenue(venueConverter.toDTO(event.getHall().getVenue()));
    result.setHall(hallConverter.toDTO(event.getHall()));
    result.setEventNote(event.getExternalNote());
    result.setEventSerial(serialConverter.toDTO(event.getEventSerial()));
    result.setTicketLimit(event.getTicketLimit() == null ? event.getHall().getCapacity() : event.getTicketLimit());
    result.setMandatory(event.isMandatory());
    result.setReservable(event.isReservable());
    result.setStatus(event.getStatus().toString());

    // add child events
    EventFilter eventFilter = new EventFilter();
    eventFilter.setParentEvent(event);
    List<Event> childEventList = events.list(eventFilter).getItems();
    if (childEventList != null && !childEventList.isEmpty()) {
      result.setChildEvents(childEventList.stream().map(e -> toDTO(e)).toList());
    }

    // add prices
    EventTicketPriceFilter priceFilter = new EventTicketPriceFilter();
    priceFilter.setEvent(event);
    List<EventTicketPrice> priceList = prices.list(priceFilter).getItems();

    if (priceList != null && !priceList.isEmpty()) {
      List<EventTicketPriceDTO> priceDTOs = priceList.stream()
              .map(p -> priceConverter.toDTO(p))
              .toList();
      result.setPrices(priceDTOs);
    }

    // add seat categories and availability
    List<SeatCategory> seatCategoryList = capacityCalculator.getSeats(event);
    if (seatCategoryList != null && !seatCategoryList.isEmpty()) {
      result.setAvailableTickets(seatCategoryList.stream()
              .collect(
                      Collectors.toMap(SeatCategory::getName,
                              SeatCategory::getSeatsAvailable)));
    }

    return result;
  }

  public Event fromDTO(EventDTO dto) {

    if (dto == null) {
      return null;
    }

    if (dto.getId() != null) {
      return events.get(dto.getId());
    }

    Event result = new Event();
    result.setSeason(seasonConverter.fromDTO(dto.getSeason()));
    result.setDate(dto.getDate());
    result.setTime(dto.getTime());
    result.setProduction(productionVersionConverter.fromDTO(dto.getVersion(), null, true));
    result.setHall(hallConverter.fromDTO(dto.getHall()));
    result.setStatus(EventStatus.valueOf(dto.getStatus().toString()));

    return result;
  }
}
