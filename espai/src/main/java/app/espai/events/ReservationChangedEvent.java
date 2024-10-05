/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.events;

import app.espai.model.Reservation;
import app.espai.model.ReservationStatus;

/**
 *
 * @author rborowski
 */
public class ReservationChangedEvent {

  private ReservationStatus oldStatus;
  private ReservationStatus newStatus;
  private Reservation reservation;

  public ReservationChangedEvent() {

  }

  public ReservationChangedEvent(Reservation reservation, ReservationStatus oldStatus,
          ReservationStatus newStatus) {

    this.reservation = reservation;
    this.oldStatus = oldStatus;
    this.newStatus = newStatus;
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the oldStatus
   */
  public ReservationStatus getOldStatus() {
    return oldStatus;
  }

  /**
   * @param oldStatus the oldStatus to set
   */
  public void setOldStatus(ReservationStatus oldStatus) {
    this.oldStatus = oldStatus;
  }

  /**
   * @return the newStatus
   */
  public ReservationStatus getNewStatus() {
    return newStatus;
  }

  /**
   * @param newStatus the newStatus to set
   */
  public void setNewStatus(ReservationStatus newStatus) {
    this.newStatus = newStatus;
  }

  /**
   * @return the reservation
   */
  public Reservation getReservation() {
    return reservation;
  }

  /**
   * @param reservation the reservation to set
   */
  public void setReservation(Reservation reservation) {
    this.reservation = reservation;
  }
  //</editor-fold>

}
