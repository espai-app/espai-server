package app.espai.views;

import app.espai.dao.Users;
import app.espai.model.User;
import app.espai.views.users.UserPasswordManager;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class ResetPasswordView implements Serializable {

  @EJB
  private Users users;

  @EJB
  private UserPasswordManager userPasswordManager;

  private User user;
  private String newPassword;
  private String newPasswordRetype;

  @PostConstruct
  public void init() {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String token = econtext.getRequestParameterMap().get("token");
    if (token == null) {
      context.addMessage(null, new FacesMessage(
              FacesMessage.SEVERITY_ERROR,
              "Der Link zum Zurücksetzen des Passwortes ist nicht vollständig.",
              ""));
      return;
    }

    user = users.getByResetToken(token);
    if (user == null || user.getResetTokenValid().before(new Date())) {
      context.addMessage(null, new FacesMessage(
              FacesMessage.SEVERITY_ERROR,
              "Der Token ist abgelaufen oder ungültig. Bitte setzen Sie das Passwort erneut zurück.",
              ""));
    }
  }

  public void updatePassword() throws IOException {

    FacesContext context = FacesContext.getCurrentInstance();

    if (newPassword == null || newPassword.isBlank() || newPassword.length() < 8) {
      context.addMessage(null, new FacesMessage(
              FacesMessage.SEVERITY_ERROR,
              "Das Passwort darf nicht leer sein und muss aus min. 8 Zeichen bestehen.",
              ""));
      return;
    }

    if (!newPassword.equals(newPasswordRetype)) {
      context.addMessage(null, new FacesMessage(
              FacesMessage.SEVERITY_ERROR,
              "Die beiden Passwörter stimmen nicht überein.",
              ""));
      return;
    }

    userPasswordManager.setPassword(user, newPassword);
    user.setResetToken(null);
    user.setResetTokenValid(null);
    users.save(user);

    context.addMessage(null, new FacesMessage(
              FacesMessage.SEVERITY_INFO,
              "Ihr Passwort wurde erfolgreich geändert.",
              ""));
    context.getExternalContext().getFlash().setKeepMessages(true);
    context.getExternalContext().redirect("login.xhtml");
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
   * @return the newPassword
   */
  public String getNewPassword() {
    return newPassword;
  }

  /**
   * @param newPassword the newPassword to set
   */
  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  /**
   * @return the newPasswordRetype
   */
  public String getNewPasswordRetype() {
    return newPasswordRetype;
  }

  /**
   * @param newPasswordRetype the newPasswordRetype to set
   */
  public void setNewPasswordRetype(String newPasswordRetype) {
    this.newPasswordRetype = newPasswordRetype;
  }
  //</editor-fold>

}
