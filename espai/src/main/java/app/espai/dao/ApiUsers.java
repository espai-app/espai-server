/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */
package app.espai.dao;

import app.espai.model.ApiUser;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 * DAO class for apiUser
 */
@Named
@Stateless
public class ApiUsers extends AbstractApiUsers {

  public ApiUser getByToken(String token) throws ResourceNotFoundException {

    try {
      TypedQuery<ApiUser> query = em.createQuery(
              "SELECT a FROM ApiUser a WHERE a.apiToken = :token",
              ApiUser.class);

      query.setParameter("token", token);

      return query.getSingleResult();
    } catch (NonUniqueResultException ex) {
      throw new ResourceNotFoundException("Token not found.");
    }
  }

}
