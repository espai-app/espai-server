/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.events;

import app.espai.model.SeatCategory;

/**
 *
 * @author rborowski
 */
public class SeatCategoryChangedEvent {

  private SeatCategory seatCategory;

  public SeatCategoryChangedEvent() {

  }

  public SeatCategoryChangedEvent(SeatCategory seatCategory) {
    this.seatCategory = seatCategory;
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
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
