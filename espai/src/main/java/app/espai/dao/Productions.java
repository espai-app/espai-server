/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Production;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import java.util.Comparator;

/**
 * DAO class for production
 */
@Named
@Stateless
public class Productions extends AbstractProductions {

  public static final Comparator<Production> DEFAULT_ORDER = new Comparator<>() {
    @Override
    public int compare(Production o1, Production o2) {
      if (o1 == null) {
        return 1;
      }

      if (o2 == null) {
        return -1;
      }
      return String.CASE_INSENSITIVE_ORDER.compare(
              o1.getTitle(), o2.getTitle());
    }
  };

}