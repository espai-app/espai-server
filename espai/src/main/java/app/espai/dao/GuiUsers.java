/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.GuiUser;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 * DAO class for guiUser
 */
@Named
@Stateless
public class GuiUsers extends AbstractGuiUsers {

  public GuiUser getByUsername(String username) throws ResourceNotFoundException {

    try {
      TypedQuery<GuiUser> query = em.createQuery(
              "SELECT u FROM GuiUser u WHERE u.username = :username",
              GuiUser.class);
      query.setParameter("username", username);

      return query.getSingleResult();
    } catch (NoResultException | NonUniqueResultException ex) {
      return null;
    }
  }

  public GuiUser getByResetToken(String token) throws ResourceNotFoundException {

    try {
      TypedQuery<GuiUser> query = em.createQuery(
              "SELECT u FROM GuiUser u WHERE u.resetToken = :token",
              GuiUser.class);
      query.setParameter("token", token);

      return query.getSingleResult();
    } catch (NoResultException | NonUniqueResultException ex) {
      return null;
    }
  }

}