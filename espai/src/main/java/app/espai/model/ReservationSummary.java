/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.model;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;

/**
 * Model class for reservationSummary
 */
@Entity
@Cacheable(false)
public class ReservationSummary extends AbstractReservationSummary {

  private static final long serialVersionUID = 1L;

}