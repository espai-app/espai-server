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
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class MailAccountIndexView {

  @EJB
  private MailAccounts mailAccounts;

  private List<? extends MailAccount> mailAccountList;

  @PostConstruct
  public void init() {
    mailAccountList = mailAccounts.list().getItems();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the mailAccountList
   */
  public List<? extends MailAccount> getMailAccountList() {
    return mailAccountList;
  }

  /**
   * @param mailAccountList the mailAccountList to set
   */
  public void setMailAccountList(List<? extends MailAccount> mailAccountList) {
    this.mailAccountList = mailAccountList;
  }
  //</editor-fold>

}
