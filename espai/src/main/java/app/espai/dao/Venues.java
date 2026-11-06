/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Venue;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.Comparator;
import java.util.List;

/**
 * DAO class for venue
 */
@Named
@Stateless
public class Venues extends AbstractVenues {

  public Venue getBySlug(String slug) {

    TypedQuery<Venue> query = em.createQuery("SELECT v FROM Venue v WHERE v.slug = :slug", Venue.class);
    query.setParameter("slug", slug);

    List<Venue> result = query.getResultList();

    if (result.isEmpty()) {
      return null;
    }

    return result.get(0);
  }

  public static final Comparator<Venue> DEFAULT_ORDER = new Comparator<>() {
    @Override
    public int compare(Venue o1, Venue o2) {
      if (o1 == null) {
        return 1;
      }

      if (o2 == null) {
        return -1;
      }
      return String.CASE_INSENSITIVE_ORDER.compare(
              o1.getCity() + o1.getName(), o2.getCity() + o2.getName());
    }
  };

}