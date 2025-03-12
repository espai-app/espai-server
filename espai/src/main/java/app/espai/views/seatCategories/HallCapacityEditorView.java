/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.seatCategories;

import app.espai.dao.HallCapacities;
import app.espai.dao.Halls;
import app.espai.dao.SeatCategories;
import app.espai.filter.SeatCategoryFilter;
import app.espai.model.Hall;
import app.espai.model.HallCapacity;
import app.espai.model.SeatCategory;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.util.List;
import org.primefaces.PrimeFaces;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class HallCapacityEditorView {

  @EJB
  private Halls halls;

  @EJB
  private HallCapacities hallCapacities;
  
  @EJB
  private SeatCategories seatCategories;

  private Hall hall;
  private HallCapacity hallCapacity;
  private List<SeatCategory> seatCategoryList;

  @PostConstruct
  public void init() {

    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String hallIdString = econtext.getRequestParameterMap().get("hallId");
    String hallCapacityIdString = econtext.getRequestParameterMap().get("hallCapacityId");

    if (hallIdString == null && hallCapacityIdString == null) {
      throw new RuntimeException("hallId or hallCapacityId missing.");
    }

    if (hallCapacityIdString != null && !hallCapacityIdString.isBlank()) {
      hallCapacity = hallCapacities.get(Long.parseLong(hallCapacityIdString));
      hall = hallCapacity.getHall();
    } else if (hallIdString != null && !hallIdString.isBlank()) {
      hall = halls.get(Long.parseLong(hallIdString));

      hallCapacity = new HallCapacity();
      hallCapacity.setHall(hall);
    }

    if (hall == null) {
      throw new RuntimeException("Hall missing.");
    }
    
    seatCategoryList = seatCategories.list(new SeatCategoryFilter()).getItems();
  }

  public void save() {
    hallCapacities.save(hallCapacity);
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
   * @return the hallCapacity
   */
  public HallCapacity getHallCapacity() {
    return hallCapacity;
  }
  
  /**
   * @param hallCapacity the hallCapacity to set
   */
  public void setHallCapacity(HallCapacity hallCapacity) {
    this.hallCapacity = hallCapacity;
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
  //</editor-fold>
  
}
