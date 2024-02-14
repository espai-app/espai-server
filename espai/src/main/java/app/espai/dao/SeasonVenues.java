/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Season;
import app.espai.model.Venue;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO class for seasonVenue
 */
@Named
@Stateless
public class SeasonVenues extends AbstractSeasonVenues {

  public List<Venue> getVenues(Season season) {
    TypedQuery<Venue> query = em.createQuery("SELECT s.venue FROM SeasonVenue s "
            + "WHERE s.season = :season ORDER BY s.venue.city, s.venue.name", Venue.class);
    query.setParameter("season", season);

    return query.getResultList();
  }

}