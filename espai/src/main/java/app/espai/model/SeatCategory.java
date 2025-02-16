/**
 * This software was created with xprs. 
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

/**
 * Model class for seatCategory
 */
@Entity
public class SeatCategory extends AbstractSeatCategory {

  private static final long serialVersionUID = 1L;
  
  @Transient
  private int seatsAvailable = 0;
  
  @Transient
  private int seatsTaken = 0;
  
  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the seatsAvailable
   */
  public int getSeatsAvailable() {
    return seatsAvailable;
  }
  
  /**
   * @param seatsAvailable the seatsAvailable to set
   */
  public void setSeatsAvailable(int seatsAvailable) {
    this.seatsAvailable = seatsAvailable;
  }

  /**
   * @return the seatsTaken
   */
  public int getSeatsTaken() {
    return seatsTaken;
  }

  /**
   * @param seatsTaken the seatsTaken to set
   */
  public void setSeatsTaken(int seatsTaken) {
    this.seatsTaken = seatsTaken;
  }
  //</editor-fold>
  
}