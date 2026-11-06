/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Production;
import app.espai.model.ProductionVersion;
import app.espai.model.Season;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO class for productionVersion
 */
@Named
@Stateless
public class ProductionVersions extends AbstractProductionVersions {
  
  public List<ProductionVersion> list(Production production) {
    TypedQuery<ProductionVersion> query = em.createQuery(
            "SELECT v FROM ProductionVersion v WHERE v.production = :production", 
            ProductionVersion.class);
    query.setParameter("production", production);
    
    return query.getResultList();
  }
  
  public List<ProductionVersion> listProgrammed(Production production, Season season) {
    
    TypedQuery<ProductionVersion> query = em.createQuery(
            "SELECT DISTINCT e.production "
                    + "FROM Event e "
                    + "WHERE e.season = :season "
                    + "AND e.production.production = :production "
                    + "AND e.hidden = false", 
            ProductionVersion.class);
    query.setParameter("season", season);
    query.setParameter("production", production);
    
    return query.getResultList();
  }

}