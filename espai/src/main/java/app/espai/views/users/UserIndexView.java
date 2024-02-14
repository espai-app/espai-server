package app.espai.views.users;

import app.espai.dao.Users;
import app.espai.model.User;
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
public class UserIndexView {

  @EJB
  private Users users;

  private List<? extends User> userList;

  @PostConstruct
  public void init() {
    userList = users.list().getItems();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the userList
   */
  public List<? extends User> getUserList() {
    return userList;
  }

  /**
   * @param userList the userList to set
   */
  public void setUserList(List<? extends User> userList) {
    this.userList = userList;
  }
  //</editor-fold>

}
