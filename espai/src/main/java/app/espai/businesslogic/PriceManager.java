/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.businesslogic;

import app.espai.dao.EventTicketPriceTemplates;
import app.espai.dao.HallCapacities;
import app.espai.filter.EventTicketPriceTemplateFilter;
import app.espai.filter.HallCapacityFilter;
import app.espai.model.Event;
import app.espai.model.EventTicketPrice;
import app.espai.model.EventTicketPriceTemplate;
import app.espai.model.HallCapacity;
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
  private HallCapacities hallCapacities;

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
      
      HallCapacityFilter capacityFilter = new HallCapacityFilter();
      capacityFilter.setHall(event.getHall());
      List<HallCapacity> hallCapacityList = hallCapacities.list(capacityFilter).getItems();

      for (HallCapacity s : hallCapacityList) {
        for (EventTicketPriceTemplate t : templates) {
          EventTicketPrice r = new EventTicketPrice();
          r.setEvent(event);
          r.setSeatCategory(s.getSeatCategory());
          r.setPriceCategory(t.getPriceCategory());
          r.setPrice(t.getPrice());
          result.add(r);
        }
      }
    }

    return result;

  }
}
