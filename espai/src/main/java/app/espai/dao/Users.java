/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */
package app.espai.dao;

import app.espai.model.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 * DAO class for user
 */
@Named
@Stateless
public class Users extends AbstractUsers {

  public User getByUsername(String username) throws ResourceNotFoundException {

    try {
      TypedQuery<User> query = em.createQuery(
              "SELECT u FROM User u WHERE u.username = :username",
              User.class);
      query.setParameter("username", username);

      return query.getSingleResult();
    } catch (NoResultException | NonUniqueResultException ex) {
      return null;
    }
  }

  public User getByResetToken(String token) throws ResourceNotFoundException {

    try {
      TypedQuery<User> query = em.createQuery(
              "SELECT u FROM User u WHERE u.resetToken = :token",
              User.class);
      query.setParameter("token", token);

      return query.getSingleResult();
    } catch (NoResultException | NonUniqueResultException ex) {
      return null;
    }
  }

}
