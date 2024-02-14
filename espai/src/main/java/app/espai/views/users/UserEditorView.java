package app.espai.views.users;

import static app.espai.Constants.USER_ID;
import app.espai.dao.AccessRights;
import app.espai.dao.Users;
import app.espai.dao.Venues;
import app.espai.filter.AccessRightFilter;
import app.espai.model.AccessRight;
import app.espai.model.User;
import app.espai.model.UserRole;
import app.espai.model.Venue;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class UserEditorView extends BaseView implements Serializable {

  @EJB
  private Users users;

  @EJB
  private AccessRights accessRights;

  @EJB
  private Venues venues;

  @EJB
  private UserPasswordManager userPasswordManager;

  private User user;
  private List<AccessRight> accessRightList = new LinkedList<>();
  private List<? extends Venue> venueList;
  private final UserRole[] userRoleList = UserRole.values();

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String userParam = econtext.getRequestParameterMap().get(USER_ID);
    if (userParam == null || !userParam.matches("\\d+")) {
      user = new User();
    } else {
      user = users.get(Long.parseLong(userParam));

      AccessRightFilter rightFilter = new AccessRightFilter();
      rightFilter.setUser(user);
      accessRightList.addAll(accessRights.list(rightFilter).getItems());
    }

    venueList = venues.list().getItems();
    venueList.sort(Venues.DEFAULT_ORDER);
  }

  public void addAccessRight() {
    accessRightList.add(new AccessRight());
  }

  public void removeAccessRight(int index) {
    if (index >= 0 && index < accessRightList.size()) {
      AccessRight r = accessRightList.get(index);
      if (r.getId() != null) {
        accessRights.delete(r);
      }
      accessRightList.remove(index);
    }
  }

  public void save() {
    user = users.save(user);

    for (AccessRight r : accessRightList) {
      r.setUser(user);
      if (r.getRole() == UserRole.MANAGER) {
      r.setVenue(null);
    }
      accessRights.save(r);
    }

    redirect("editor.xhtml?userId=" + user.getId(),
            "Benutzer gespeichert",
            "Ihre Änderungen wurden gespeichert.",
            FacesMessage.SEVERITY_INFO);
  }

  public void delete() {
    users.delete(user);
    redirect("index.xhtml",
            "Benuterkonto gelöscht",
            String.format("Das Benutzerkonto für %s wurde gelöscht.", user.getDisplayName()),
            FacesMessage.SEVERITY_INFO);
  }

  public void resetPassword() {
    userPasswordManager.resetPassword(user);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * @return the accessRightList
   */
  public List<AccessRight> getAccessRightList() {
    return accessRightList;
  }

  /**
   * @param accessRightList the accessRightList to set
   */
  public void setAccessRightList(List<AccessRight> accessRightList) {
    this.accessRightList = accessRightList;
  }

  /**
   * @return the venueList
   */
  public List<? extends Venue> getVenueList() {
    return venueList;
  }

  /**
   * @param venueList the venueList to set
   */
  public void setVenueList(List<? extends Venue> venueList) {
    this.venueList = venueList;
  }
  //</editor-fold>

  /**
   * @return the userRoleList
   */
  public UserRole[] getUserRoleList() {
    return userRoleList;
  }
}
