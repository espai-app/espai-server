/**
 * This software was created with xprs. 
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.model;

import jakarta.persistence.Entity;

/**
 * Model class for ticketSummary
 */
@Entity
public class TicketSummary extends AbstractTicketSummary {
  
  public static final String SEATS_FREE_EVENT = "free";
  public static final String SEATS_TAKEN_EVENT = "event";
  public static final String SEATS_TAKEN_DAY = "day";

  private static final long serialVersionUID = 1L;
  
}