/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.Hall;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import java.util.Comparator;

/**
 * DAO class for hall
 */
@Named
@Stateless
public class Halls extends AbstractHalls {

  public static final Comparator<Hall> DEFAULT_ORDER = new Comparator<>() {
    @Override
    public int compare(Hall o1, Hall o2) {
      if (o1 == null) {
        return -1;
      }

      if (o2 == null) {
        return 1;
      }

      if (o1.getName().matches(".* \\d+") && o2.getName().matches(".* \\d+")) {
        int o1LastSpace = o1.getName().lastIndexOf(" ");
        String o1prefix = o1.getName().substring(0, o1LastSpace);
        int o1count = Integer.parseInt(o1.getName().substring(o1LastSpace + 1) );

        int o2LastSpace = o2.getName().lastIndexOf(" ");
        String o2prefix = o2.getName().substring(0, o2LastSpace);
        int o2count = Integer.parseInt(o2.getName().substring(o2LastSpace + 1));

        int prefixCompare = String.CASE_INSENSITIVE_ORDER.compare(o1prefix, o2prefix);
        if (prefixCompare == 0) {
          return Integer.compare(o1count, o2count);
        }
      }

      return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
    }
  };

}