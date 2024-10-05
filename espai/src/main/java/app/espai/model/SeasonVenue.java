/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.model;

import app.espai.dao.Venues;
import jakarta.persistence.Entity;
import java.util.Comparator;

/**
 * Model class for seasonVenue
 */
@Entity
public class SeasonVenue extends AbstractSeasonVenue {

  private static final long serialVersionUID = 1L;

  public static final Comparator<SeasonVenue> DEFAULT_ORDER = new Comparator<>() {
    @Override
    public int compare(SeasonVenue o1, SeasonVenue o2) {
      return Venues.DEFAULT_ORDER.compare(o1.getVenue(), o2.getVenue());
    }
  };

}