package app.espai.auth;

import app.espai.model.AccessRight;
import app.espai.model.User;
import app.espai.model.UserRole;
import app.espai.model.Venue;
import jakarta.security.enterprise.CallerPrincipal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author rborowski
 */
public class EspaiPrincipal extends CallerPrincipal {

  private final User user;
  private final List<AccessRight> accessRights;

  public EspaiPrincipal(User user, List<AccessRight> accessRights) {
    super(user.getDisplayName());
    this.user = user;
    this.accessRights = accessRights;
  }

  public boolean hasAccess(UserRole role, Venue venue) {
    for (AccessRight a : accessRights) {
      if (a.getRole() == role && Objects.equals(a.getVenue(), venue) || a.getRole() == UserRole.MANAGER) {
        return true;
      }
    }
    return false;
  }

  public Set<String> getRoles() {
    return accessRights.stream().map(i -> i.getRole().toString()).collect(Collectors.toSet());
  }

  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @return the accessRights
   */
  public List<AccessRight> getAccessRights() {
    return accessRights;
  }

}
