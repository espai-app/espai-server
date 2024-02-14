package app.espai.views;

import app.espai.dao.Users;
import app.espai.model.User;
import app.espai.views.users.UserPasswordManager;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class ForgotPasswordView extends BaseView {

  @EJB
  private Users users;

  @EJB
  private UserPasswordManager userPasswordManager;

  private String username;

  public void requestPassword() {
    User user = users.getByUsername(username);
    if (user != null) {
      userPasswordManager.resetPassword(user);
    }

    redirect("login.xhtml",
            "Wenn es ein Konto zu dieser E-Mail gibt, erhalten Sie in Kürze einen Link per E-Mail um Ihr Passwort zurücksetzen zu können.",
            "",
            FacesMessage.SEVERITY_INFO);

  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }
  //</editor-fold>

}
