package app.espai.views;

import app.espai.auth.EspaiPrincipal;
import app.espai.dao.Halls;
import app.espai.filter.EventFilter;
import app.espai.filter.HallFilter;
import app.espai.model.AccessRight;
import app.espai.model.Hall;
import app.espai.model.UserRole;
import app.espai.model.Venue;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author rborowski
 */
@Named
@Stateless
public class UserContext implements Serializable {

  @Inject
  private SecurityContext securityContext;

  @EJB
  private Halls halls;

  public boolean isRestricted() {
    for (AccessRight a : getCurrentPrincipal().getAccessRights()) {
      if (a.getRole() == UserRole.MANAGER) {
        return false;
      }
    }
    return true;
  }

  public List<Venue> getVenues() {
    return Collections.unmodifiableList(
            getCurrentPrincipal().getAccessRights().stream()
                    .map(AccessRight::getVenue)
                    .distinct()
                    .collect(Collectors.toList()));
  }

  public EventFilter getEventFilter() {

    List<Hall> hallList = halls.list(getHallFilter()).getItems();

    EventFilter filter = new EventFilter();
    filter.setHalls(hallList);
    return filter;
  }

  public HallFilter getHallFilter() {
    HallFilter filter = new HallFilter();
    filter.setVenues(getVenues());
    return filter;
  }

  /**
   * @return the currentPrincipal
   */
  public EspaiPrincipal getCurrentPrincipal() {
    return (EspaiPrincipal) securityContext.getCallerPrincipal();
  }

}
