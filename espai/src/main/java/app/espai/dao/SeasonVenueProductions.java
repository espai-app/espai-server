/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Production;
import app.espai.model.Season;
import app.espai.model.SeasonVenueProduction;
import app.espai.model.Venue;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;

/**
 * DAO class for seasonVenueProduction
 */
@Named
@Stateless
public class SeasonVenueProductions extends AbstractSeasonVenueProductions {

  public boolean exists(Season season, Venue venue, Production production) {

    TypedQuery<SeasonVenueProduction> query = em.createQuery(
            "SELECT p FROM SeasonVenueProduction p "
                    + "WHERE p.season = :season "
                    + "AND p.venue = :venue "
                    + "AND p.production = :production",
            SeasonVenueProduction.class);

    query.setParameter("season", season);
    query.setParameter("venue", venue);
    query.setParameter("production", production);

    return !query.getResultList().isEmpty();
  }

}