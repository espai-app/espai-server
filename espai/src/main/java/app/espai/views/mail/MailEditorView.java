package app.espai.views.mail;

import app.espai.Mailer;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import java.io.IOException;
import org.primefaces.PrimeFaces;
import rocks.xprs.mail.Mail;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class MailEditorView {

  @EJB
  private Mailer mailer;

  private Mail mail;

  @PostConstruct
  public void init() {
    mail = (Mail) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("mail");
    if (mail == null) {
      mail = new Mail();
    }
  }

  public void sendMail() {
    try {
      Message message = mailer.send(mail);
      mailer.saveSent(message);
    } catch (MessagingException | IOException ex) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
              FacesMessage.SEVERITY_ERROR,
              "E-Mail nicht gesendet",
              ex.getMessage()));
    }
    PrimeFaces.current().dialog().closeDynamic(null);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the mail
   */
  public Mail getMail() {
    return mail;
  }

  /**
   * @param mail the mail to set
   */
  public void setMail(Mail mail) {
    this.mail = mail;
  }
  //</editor-fold>

}
