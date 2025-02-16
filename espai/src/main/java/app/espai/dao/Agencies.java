/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Agency;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO class for agency
 */
@Named
@Stateless
public class Agencies extends AbstractAgencies {

  public Agency getByName(String name) {
    TypedQuery<Agency> query = em.createQuery(
            "SELECT a FROM Agency a WHERE a.name = :name",
            Agency.class);
    query.setParameter("name", name);

    List<Agency> result = query.getResultList();
    if (result.isEmpty()) {
      return null;
    }

    return result.get(0);
  }

}