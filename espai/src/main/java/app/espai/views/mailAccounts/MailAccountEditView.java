/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.mailAccounts;

import app.espai.dao.MailAccounts;
import app.espai.model.MailAccount;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class MailAccountEditView {

  @EJB
  private MailAccounts mailAccounts;

  private MailAccount mailAccount;

  @PostConstruct
  public void init() {

    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String mailAccountIdParam = econtext.getRequestParameterMap().get("mailAccountId");
    if (mailAccountIdParam != null && mailAccountIdParam.matches("\\d+")) {
      mailAccount = mailAccounts.get(Long.parseLong(mailAccountIdParam));
    } else {
      mailAccount = new MailAccount();
    }
  }

  public void save() {
    mailAccounts.save(mailAccount);

    FacesContext context = FacesContext.getCurrentInstance();
    context.addMessage(
            null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Mailkonto gespeichert.", null));

    try {
      context.getExternalContext().redirect("index.xhtml");
    } catch (IOException ex) {
      Logger.getLogger(MailAccountEditView.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

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
