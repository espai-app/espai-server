/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Production;
import app.espai.model.Season;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO class for seasonProduction
 */
@Named
@Stateless
public class SeasonProductions extends AbstractSeasonProductions {

  public List<Production> getProductions(Season season) {
    TypedQuery<Production> query = em.createQuery("SELECT s.production "
            + "FROM SeasonProduction s "
            + "WHERE s.season = :season "
            + "ORDER BY s.production.title", Production.class);
    query.setParameter("season", season);

    return query.getResultList();
  }

}