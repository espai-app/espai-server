package app.espai.views.mailTemplates;

import app.espai.dao.MailTemplates;
import app.espai.model.MailTemplate;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
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

    try {
      FacesContext context = FacesContext.getCurrentInstance();
      ExternalContext econtext = context.getExternalContext();

      if (mailTemplate.getSeason() != null) {
        econtext.redirect(String.format(
                "../../seasons/settings/mail.xhtml?seasonId=%d", 
                mailTemplate.getSeason().getId()));
      } else {
        econtext.redirect("index.xhtml");
      }
      
      context.responseComplete();
    } catch (IOException ex) {
      Logger.getLogger(MailTemplateEditorView.class.getName()).log(
              Level.SEVERE, 
              "Could not redirect.", 
              ex);
    }

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
