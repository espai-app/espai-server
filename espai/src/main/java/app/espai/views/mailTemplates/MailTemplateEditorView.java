package app.espai.views.mailTemplates;

import app.espai.dao.MailAccounts;
import app.espai.dao.MailTemplates;
import app.espai.model.MailAccount;
import app.espai.model.MailTemplate;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Named
@Stateful
@RequestScoped
@RolesAllowed({"MANAGER"})
public class MailTemplateEditorView {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private MailTemplates mailTemplates;

  @EJB
  private MailAccounts mailAccounts;

  private MailTemplate mailTemplate;

  private List<? extends MailAccount> mailAccountList;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String templateIdParam = econtext.getRequestParameterMap().get("templateId");
    if (templateIdParam == null || !templateIdParam.matches("\\d+")) {
      mailTemplate = new MailTemplate();
      mailTemplate.setSeason(seasonContext.getCurrentSeason());
    } else {
      mailTemplate = mailTemplates.get(Long.parseLong(templateIdParam));
    }

    mailAccountList = mailAccounts.list().getItems();
  }

  public void save() {
    mailTemplates.save(mailTemplate);
    FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage("E-Mail-Vorlage gespeichert."));
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the mailTemplate
   */
  public MailTemplate getMailTemplate() {
    return mailTemplate;
  }

  /**
   * @param mailTemplate the mailTemplate to set
   */
  public void setMailTemplate(MailTemplate mailTemplate) {
    this.mailTemplate = mailTemplate;
  }

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
