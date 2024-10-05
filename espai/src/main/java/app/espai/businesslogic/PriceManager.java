/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.businesslogic;

import app.espai.dao.EventTicketPriceTemplates;
import app.espai.dao.SeatCategories;
import app.espai.filter.EventTicketPriceTemplateFilter;
import app.espai.filter.SeatCategoryFilter;
import app.espai.model.Event;
import app.espai.model.EventTicketPrice;
import app.espai.model.EventTicketPriceTemplate;
import app.espai.model.SeatCategory;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Stateless
public class PriceManager {

  @EJB
  private SeatCategories seatCategories;

  @EJB
  private EventTicketPriceTemplates priceTemplates;

  public List<EventTicketPrice> getPrices(Event event) {

    List<EventTicketPriceTemplate> templates;

    EventTicketPriceTemplateFilter filter = new EventTicketPriceTemplateFilter();
    filter.setHall(event.getHall());
    templates = priceTemplates.list(filter).getItems();

    List<EventTicketPrice> result = new LinkedList<>();

    if (!templates.isEmpty()) {
      for (EventTicketPriceTemplate t : templates) {
        EventTicketPrice r = new EventTicketPrice();
        r.setEvent(event);
        r.setSeatCategory(t.getSeatCategory());
        r.setPriceCategory(t.getPriceCategory());
        r.setPrice(t.getPrice());
        result.add(r);
      }
    } else {
      filter.setVenue(event.getHall().getVenue());
      filter.setHallIsNull(true);
      templates = priceTemplates.list(filter).getItems();

      SeatCategoryFilter seatCatFilter = new SeatCategoryFilter();
      seatCatFilter.setHall(event.getHall());
      List<SeatCategory> seatCategoryList = seatCategories.list(seatCatFilter).getItems();

      for (SeatCategory s : seatCategoryList) {
        for (EventTicketPriceTemplate t : templates) {
          EventTicketPrice r = new EventTicketPrice();
          r.setEvent(event);
          r.setSeatCategory(s);
          r.setPriceCategory(t.getPriceCategory());
          r.setPrice(t.getPrice());
          result.add(r);
        }
      }
    }

    return result;

  }
}
