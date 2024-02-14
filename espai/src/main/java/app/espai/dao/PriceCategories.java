/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.model.PriceCategory;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import java.util.Comparator;
import java.util.Objects;

/**
 * DAO class for priceCategory
 */
@Named
@Stateless
public class PriceCategories extends AbstractPriceCategories {

  public static Comparator<PriceCategory> DEFAULT_ORDER = new Comparator<>() {
    @Override
    public int compare(PriceCategory o1, PriceCategory o2) {
      if (Objects.equals(o1.getPosition(), o2.getPosition())) {
        return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
      }

      return (o1.getPosition() < o2.getPosition()) ? -1 : 1;
    }
  };

}