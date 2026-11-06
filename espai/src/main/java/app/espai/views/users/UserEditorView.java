package app.espai.views.users;

import static app.espai.Constants.USER_ID;
import app.espai.dao.AccessRights;
import app.espai.dao.Users;
import app.espai.dao.Venues;
import app.espai.filter.AccessRightFilter;
import app.espai.model.AccessRight;
import app.espai.model.ApiUser;
import app.espai.model.GuiUser;
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
import java.util.UUID;

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

  private GuiUser guiUser;
  private ApiUser apiUser;
  private List<AccessRight> accessRightList = new LinkedList<>();
  private List<? extends Venue> venueList;
  private final UserRole[] userRoleList = UserRole.values();

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String userParam = econtext.getRequestParameterMap().get(USER_ID);
    String userTypeParam = econtext.getRequestParameterMap().get("userType");
    if (userParam == null || !userParam.matches("\\d+")) {

      if ("apiUser".equals(userTypeParam)) {
        apiUser = new ApiUser();
        apiUser.setApiToken(UUID.randomUUID().toString());
      } else {
        guiUser = new GuiUser();
      }
    } else {
      User genericUser = users.get(Long.parseLong(userParam));

      if (genericUser instanceof ApiUser u) {
        apiUser = u;
      } else if (genericUser instanceof GuiUser u) {
        guiUser = u;
      }

      AccessRightFilter rightFilter = new AccessRightFilter();
      rightFilter.setUser(genericUser);
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

    User user = null;
    if (apiUser != null) {
      user = users.save(apiUser);
    } else if (guiUser != null) {
      user = users.save(guiUser);
    }

    if (user != null) {
      for (AccessRight r : accessRightList) {
        r.setUser(user);
        if (r.getRole() == UserRole.MANAGER) {
        r.setVenue(null);
      }
        accessRights.save(r);
      }
    }

    redirect("editor.xhtml?userId=" + user.getId(),
            "Benutzer gespeichert",
            "Ihre Änderungen wurden gespeichert.",
            FacesMessage.SEVERITY_INFO);
  }

  public void delete() {
    users.delete(guiUser);
    redirect("index.xhtml",
            "Benuterkonto gelöscht",
            String.format("Das Benutzerkonto für %s wurde gelöscht.", guiUser.getDisplayName()),
            FacesMessage.SEVERITY_INFO);
  }

  public void resetPassword() {
    userPasswordManager.resetPassword(guiUser);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the guiUser
   */
  public GuiUser getGuiUser() {
    return guiUser;
  }

  /**
   * @param guiUser the guiUser to set
   */
  public void setGuiUser(GuiUser guiUser) {
    this.guiUser = guiUser;
  }

  /**
   * @return the apiUser
   */
  public ApiUser getApiUser() {
    return apiUser;
  }

  /**
   * @param apiUser the apiUser to set
   */
  public void setApiUser(ApiUser apiUser) {
    this.apiUser = apiUser;
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
