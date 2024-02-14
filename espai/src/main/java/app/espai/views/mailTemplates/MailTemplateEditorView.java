package app.espai.views.mailTemplates;

import app.espai.dao.MailTemplates;
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

  private MailTemplate mailTemplate;

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
  //</editor-fold>

}
