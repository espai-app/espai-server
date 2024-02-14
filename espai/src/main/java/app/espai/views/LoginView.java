package app.espai.views;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class LoginView {

  private String username;
  private String password;

  @Inject
  private SecurityContext securityContext;

  public void login() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext externalContext = context.getExternalContext();
    HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
    String returnUrl = externalContext.getRequestParameterMap().get("returnUrl");

    UsernamePasswordCredential credential
            = new UsernamePasswordCredential(username, password);
    AuthenticationParameters params = new AuthenticationParameters();
    params.setCredential(credential);
    params.setNewAuthentication(true);

    AuthenticationStatus status = securityContext.authenticate(
            request, (HttpServletResponse) externalContext.getResponse(), params);

    if (status == AuthenticationStatus.SUCCESS) {
      if (returnUrl != null && !returnUrl.isBlank()) {
        externalContext.redirect(returnUrl);
      } else {
        externalContext.redirect("index.xhtml");
      }

      return;
    }

    if (status == AuthenticationStatus.SEND_FAILURE) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login fehlgeschlagen.", null));
    }
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

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }
  //</editor-fold>
}
