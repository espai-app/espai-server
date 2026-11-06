
package app.espai.views.users;

import app.espai.dao.ApiUsers;
import app.espai.filter.ApiUserFilter;
import app.espai.model.ApiUser;
import app.espai.model.GuiUser;
import app.espai.views.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class ProfileEditorView {

  @EJB
  private UserContext userContext;


  @EJB
  private ApiUsers apiUsers;

  private GuiUser user;
  private List<ApiUser> apiUserList;

  @PostConstruct
  public void init() {

    user = (GuiUser) userContext.getCurrentPrincipal().getUser();

    ApiUserFilter apiUserFilter = new ApiUserFilter();
    apiUserList = apiUsers.list(apiUserFilter).getItems();
  }

  public void saveUser() {

  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the user
   */
  public GuiUser getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(GuiUser user) {
    this.user = user;
  }

  /**
   * @return the apiUserList
   */
  public List<ApiUser> getApiUserList() {
    return apiUserList;
  }

  /**
   * @param apiUserList the apiUserList to set
   */
  public void setApiUserList(List<ApiUser> apiUserList) {
    this.apiUserList = apiUserList;
  }
  //</editor-fold>

}
