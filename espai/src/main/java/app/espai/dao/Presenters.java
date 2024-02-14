/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */
package app.espai.dao;

import app.espai.model.Presenter;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import java.util.Comparator;

/**
 * DAO class for presenter
 */
@Named
@Stateless
public class Presenters extends AbstractPresenters {

  public static final Comparator<Presenter> DEFAULT_ORDER = new Comparator<>() {
    @Override
    public int compare(Presenter o1, Presenter o2) {
      return String.CASE_INSENSITIVE_ORDER.compare(
              o1.getSurname() + o1.getGivenName(),
              o2.getSurname() + o2.getGivenName());
    }
  };

}
