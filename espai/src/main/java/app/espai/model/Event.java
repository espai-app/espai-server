/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;

/**
 * Model class for event
 */
@Entity
public class Event extends AbstractEvent {

  private static final long serialVersionUID = 1L;

  @Transient
  private transient LocalDateTime startDateTime;

  @Transient
  private transient LocalDateTime endDateTime;

  public LocalDateTime getStartDateTime() {

    if (startDateTime == null) {
      startDateTime = LocalDateTime.of(getDate(), getTime());
    }
    return startDateTime;
  }

  public int getCapacity() {
    if (this.getTicketLimit() != null) {
      return this.getTicketLimit();
    } else if (this.getHall() != null) {
      return this.getHall().getCapacity();
    } else {
      return -1;
    }
  }
}