/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Movie;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO class for movie
 */
@Named
@Stateless
public class Movies extends AbstractMovies {

  public Movie getByExternalId(String externalId) {
    TypedQuery<Movie> query = em.createQuery(
            "SELECT m FROM Movie m WHERE m.externalId = :externalId",
            Movie.class);
    query.setParameter("externalId", externalId);

    List<Movie> result = query.getResultList();
    if (result.isEmpty()) {
      return null;
    }

    return result.get(0);
  }

  public Movie getByTitle(String title) {
    TypedQuery<Movie> query = em.createQuery(
            "SELECT m FROM Movie m WHERE m.title = :title",
            Movie.class);
    query.setParameter("title", title);

    List<Movie> result = query.getResultList();
    if (result.isEmpty()) {
      return null;
    }

    return result.get(0);
  }

}