/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.events;

import app.espai.model.HallCapacity;

/**
 *
 * @author rborowski
 */
public class HallCapacityChangedEvent {

  private HallCapacity hallCapacity;

  public HallCapacityChangedEvent() {

  }

  public HallCapacityChangedEvent(HallCapacity hallCapacity) {
    this.hallCapacity = hallCapacity;
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
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
  //</editor-fold>
}
