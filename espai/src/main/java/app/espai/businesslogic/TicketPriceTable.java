/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.businesslogic;

import app.espai.model.Event;
import app.espai.model.PriceCategory;
import app.espai.model.SeatCategory;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
public interface TicketPriceTable {
  
  public Monetary getPrice(Event event, SeatCategory seatCategory, PriceCategory priceCategory);
  
}
