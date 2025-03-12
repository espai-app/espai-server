/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.eventTicketPrice;

import app.espai.dao.EventTicketPrices;
import app.espai.dao.Events;
import app.espai.dao.HallCapacities;
import app.espai.dao.PriceCategories;
import app.espai.filter.HallCapacityFilter;
import app.espai.model.Event;
import app.espai.model.EventTicketPrice;
import app.espai.model.HallCapacity;
import app.espai.model.PriceCategory;
import app.espai.model.SeatCategory;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.math.BigDecimal;
import java.util.List;
import org.primefaces.PrimeFaces;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class EventTicketPriceEditorView {

  @EJB
  private Events events;

  @EJB
  private HallCapacities hallCapacities;
  
  

  @EJB
  private PriceCategories priceCategories;

  @EJB
  private EventTicketPrices eventTicketPrices;

  private EventTicketPrice ticketPrice;
  private List<SeatCategory> seatCategoryList;
  private List<? extends PriceCategory> priceCategoryList;

  @PostConstruct
  public void init() {

    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String eventIdParam = econtext.getRequestParameterMap().get("eventId");
    String ticketPriceIdParam = econtext.getRequestParameterMap().get("ticketPriceId");

    Event event = null;
    if (ticketPriceIdParam != null && ticketPriceIdParam.matches("\\d+")) {
      ticketPrice = eventTicketPrices.get(Long.parseLong(ticketPriceIdParam));
      event = ticketPrice.getEvent();
    } else if (eventIdParam != null && eventIdParam.matches("\\d+")) {
      event = events.get(Long.parseLong(eventIdParam));
      ticketPrice = new EventTicketPrice();
      ticketPrice.setEvent(event);
      ticketPrice.setPrice(new Monetary(BigDecimal.ZERO, "EUR"));
    }
    
    HallCapacityFilter capacityFilter = new HallCapacityFilter();
    capacityFilter.setHall(event.getHall());
    seatCategoryList = hallCapacities.list(capacityFilter).getItems().stream()
            .map(HallCapacity::getSeatCategory)
            .toList();

    priceCategoryList = priceCategories.list().getItems();
  }

  public void save() {
    eventTicketPrices.save(ticketPrice);
    PrimeFaces.current().dialog().closeDynamic(null);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the ticketPrice
   */
  public EventTicketPrice getTicketPrice() {
    return ticketPrice;
  }

  /**
   * @param ticketPrice the ticketPrice to set
   */
  public void setTicketPrice(EventTicketPrice ticketPrice) {
    this.ticketPrice = ticketPrice;
  }

  /**
   * @return the seatCategoryList
   */
  public List<SeatCategory> getSeatCategoryList() {
    return seatCategoryList;
  }

  /**
   * @param seatCategoryList the seatCategoryList to set
   */
  public void setSeatCategoryList(List<SeatCategory> seatCategoryList) {
    this.seatCategoryList = seatCategoryList;
  }

  /**
   * @return the priceCategoryList
   */
  public List<? extends PriceCategory> getPriceCategoryList() {
    return priceCategoryList;
  }

  /**
   * @param priceCategoryList the priceCategoryList to set
   */
  public void setPriceCategoryList(List<? extends PriceCategory> priceCategoryList) {
    this.priceCategoryList = priceCategoryList;
  }
  //</editor-fold>

}
