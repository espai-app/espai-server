/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.priceTemplates;

import app.espai.dao.EventTicketPriceTemplates;
import app.espai.dao.Halls;
import app.espai.dao.PriceCategories;
import app.espai.dao.SeatCategories;
import app.espai.dao.Venues;
import app.espai.filter.HallFilter;
import app.espai.filter.SeatCategoryFilter;
import app.espai.model.EventTicketPriceTemplate;
import app.espai.model.Hall;
import app.espai.model.PriceCategory;
import app.espai.model.SeatCategory;
import app.espai.model.Venue;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.primefaces.PrimeFaces;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class PriceTemplateEditorView implements Serializable {

  @EJB
  private EventTicketPriceTemplates priceTemplates;

  @EJB
  private Venues venues;

  @EJB
  private Halls halls;

  @EJB
  private PriceCategories priceCategories;

  @EJB
  private SeatCategories seatCategories;

  private Venue venue;
  private EventTicketPriceTemplate priceTemplate;
  private List<Hall> hallList;
  private List<? extends PriceCategory> priceCategoryList;
  private List<SeatCategory> seatCategoryList;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String priceTemplateIdParam = econtext.getRequestParameterMap().get("priceTemplateId");
    String venueIdParam = econtext.getRequestParameterMap().get("venueId");

    if (priceTemplateIdParam != null && priceTemplateIdParam.matches("\\d+")) {
      priceTemplate = priceTemplates.get(Long.parseLong(priceTemplateIdParam));
      venue = priceTemplate.getVenue();
    } else if (venueIdParam != null && venueIdParam.matches("\\d+")) {
      venue = venues.get(Long.parseLong(venueIdParam));
      priceTemplate = new EventTicketPriceTemplate();
      priceTemplate.setVenue(venue);
      priceTemplate.setPrice(new Monetary(BigDecimal.ZERO, "EUR"));
    } else {
      throw new RuntimeException("venueId and priceTemplateId not set.");
    }

    HallFilter hallFilter = new HallFilter();
    hallFilter.setVenue(venue);
    hallList = halls.list(hallFilter).getItems();

    priceCategoryList = priceCategories.list().getItems();

    onHallChanged();
  }

  public void onHallChanged() {
    if (priceTemplate.getHall() == null) {
      seatCategoryList = Collections.EMPTY_LIST;
    }

    SeatCategoryFilter seatFilter = new SeatCategoryFilter();
    seatFilter.setHall(priceTemplate.getHall());
    seatCategoryList = seatCategories.list(seatFilter).getItems();
  }

  public void save() {
    priceTemplate.setVenue(venue);
    priceTemplates.save(priceTemplate);

    PrimeFaces.current().dialog().closeDynamic(null);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the venue
   */
  public Venue getVenue() {
    return venue;
  }

  /**
   * @param venue the venue to set
   */
  public void setVenue(Venue venue) {
    this.venue = venue;
  }

  /**
   * @return the priceTemplate
   */
  public EventTicketPriceTemplate getPriceTemplate() {
    return priceTemplate;
  }

  /**
   * @param priceTemplate the priceTemplate to set
   */
  public void setPriceTemplate(EventTicketPriceTemplate priceTemplate) {
    this.priceTemplate = priceTemplate;
  }

  /**
   * @return the hallList
   */
  public List<Hall> getHallList() {
    return hallList;
  }

  /**
   * @param hallList the hallList to set
   */
  public void setHallList(List<Hall> hallList) {
    this.hallList = hallList;
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

  /**
   * @return the seatCategorList
   */
  public List<SeatCategory> getSeatCategoryList() {
    return seatCategoryList;
  }

  /**
   * @param seatCategorList the seatCategorList to set
   */
  public void setSeatCategoryList(List<SeatCategory> seatCategorList) {
    this.seatCategoryList = seatCategorList;
  }
  //</editor-fold>

}
