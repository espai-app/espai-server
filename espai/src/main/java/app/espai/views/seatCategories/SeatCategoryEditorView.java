/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.seatCategories;

import app.espai.dao.Halls;
import app.espai.dao.SeatCategories;
import app.espai.model.Hall;
import app.espai.model.SeatCategory;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class SeatCategoryEditorView {

  @EJB
  private Halls halls;

  @EJB
  private SeatCategories seatCategories;

  private Hall hall;
  private SeatCategory seatCategory;

  @PostConstruct
  public void init() {

    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String hallIdString = econtext.getRequestParameterMap().get("hallId");
    String seatCategoryIdString = econtext.getRequestParameterMap().get("seatCategoryId");

    if (hallIdString == null && seatCategoryIdString == null) {
      throw new RuntimeException("Hall or SeatCategoryId missing.");
    }

    if (seatCategoryIdString != null && !seatCategoryIdString.isBlank()) {
      seatCategory = seatCategories.get(Long.parseLong(seatCategoryIdString));
      hall = seatCategory.getHall();
    } else if (hallIdString != null && !hallIdString.isBlank()) {
      hall = halls.get(Long.parseLong(hallIdString));

      seatCategory = new SeatCategory();
      seatCategory.setHall(hall);
    }

    if (hall == null) {
      throw new RuntimeException("Hall missing.");
    }
  }

  public void save() {
    seatCategories.save(seatCategory);
    PrimeFaces.current().dialog().closeDynamic(null);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the hall
   */
  public Hall getHall() {
    return hall;
  }

  /**
   * @param hall the hall to set
   */
  public void setHall(Hall hall) {
    this.hall = hall;
  }

  /**
   * @return the seatCategory
   */
  public SeatCategory getSeatCategory() {
    return seatCategory;
  }

  /**
   * @param seatCategory the seatCategory to set
   */
  public void setSeatCategory(SeatCategory seatCategory) {
    this.seatCategory = seatCategory;
  }
  //</editor-fold>

}
