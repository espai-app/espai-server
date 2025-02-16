package app.espai.views.mail;

import app.espai.Mailer;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
  private SeasonContext seasonContext;
  
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
      
      Mailer mailer = new Mailer(seasonContext.getCurrentSeason().getMailAccount());
      Message message = mailer.send(mail);
      mailer.saveSent(message);
      
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("E-Mail gesendet."));
    } catch (MessagingException | IOException ex) {
      Logger.getLogger(MailEditorView.class.getName()).log(Level.SEVERE, "Mailing failed.", ex);
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
              FacesMessage.SEVERITY_ERROR,
              "E-Mail nicht gesendet.",
              ex.getMessage()));
    }
    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
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
