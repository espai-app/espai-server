package app.espai.views;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rborowski
 */
public class BaseView {

  public static final String POPUP_MESSAGE = "popup";

    protected String getCurrentUrl() {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        StringBuffer url = request.getRequestURL();
        if (request.getQueryString() != null) {
            url.append("?").append(request.getQueryString());
        }

        return url.toString();
    }

    protected void redirect() {
        redirect(getCurrentUrl(), null, null, null);
    }

    protected void redirect(String url) {
        redirect(url, null, null, null);
    }

    protected void redirect(String title, String message) {
        redirect(getCurrentUrl(), title, message, FacesMessage.SEVERITY_INFO);
    }

    protected void redirect(String url, String title, String message, FacesMessage.Severity severity) {

        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

            // show messages after redirect
            externalContext.getFlash().setKeepMessages(true);

            if (title != null) {
                showMessage(title, message, severity);
            }

            externalContext.redirect(url);
        } catch (IOException ex) {
            Logger.getLogger(BaseView.class.getName()).log(
                    Level.SEVERE,
                    "Redirect failed.",
                    ex);
        }
    }

    protected void showMessage(String title, String message, FacesMessage.Severity severity) {
        FacesMessage facesMessage = new FacesMessage(
                severity != null ? severity : FacesMessage.SEVERITY_INFO,
                title,
                message);
        FacesContext.getCurrentInstance().addMessage(POPUP_MESSAGE, facesMessage);
    }

}
