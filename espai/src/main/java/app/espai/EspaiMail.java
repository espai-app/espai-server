package app.espai;

import app.espai.model.MailAccount;
import rocks.xprs.mail.Mail;

/**
 *
 * @author rborowski
 */
public class EspaiMail extends Mail {

  private MailAccount mailAccount;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the mailAccount
   */
  public MailAccount getMailAccount() {
    return mailAccount;
  }

  /**
   * @param mailAccount the mailAccount to set
   */
  public void setMailAccount(MailAccount mailAccount) {
    this.mailAccount = mailAccount;
  }
  //</editor-fold>

}
