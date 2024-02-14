/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */
package app.espai.dao;

import app.espai.model.VenueContact;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import java.util.Comparator;

/**
 * DAO class for venueContact
 */
@Named
@Stateless
public class VenueContacts extends AbstractVenueContacts {

  public static final Comparator<VenueContact> DEFAULT_ORDER = new Comparator<>() {
    @Override
    public int compare(VenueContact o1, VenueContact o2) {
      if (o1 == null) {
        return 1;
      }

      if (o2 == null) {
        return -1;
      }

      return String.CASE_INSENSITIVE_ORDER.compare(
              o1.getFamilyName() + o1.getFamilyName(),
              o2.getFamilyName() + o2.getFamilyName());
    }
  };
}
