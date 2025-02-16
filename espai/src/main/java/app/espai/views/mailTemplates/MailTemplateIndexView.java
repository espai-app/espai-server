package app.espai.views.mailTemplates;

import app.espai.dao.MailTemplates;
import app.espai.filter.MailTemplateFilter;
import app.espai.model.MailTemplate;
import app.espai.model.Season;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class MailTemplateIndexView {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private MailTemplates mailTemplates;

  private MailTemplate selectedGlobalTemplate;
  private List<MailTemplate> selectableGlobalTemplates;
  private List<MailTemplate> mailTemplateList;

  @PostConstruct
  public void init() {
    MailTemplateFilter templateFilter = new MailTemplateFilter();
    Season currentSeason = seasonContext.getCurrentSeason();

    if (currentSeason == null) {
      templateFilter.setSeasonIsNull(true);
    } else {
      templateFilter.setSeason(currentSeason);
    }

    mailTemplateList = mailTemplates.list(templateFilter).getItems();

    List<String> existingTemplates = mailTemplateList.stream()
            .map(MailTemplate::getShortCode)
            .toList();

    templateFilter.setSeasonIsNull(true);
    setSelectableGlobalTemplates(mailTemplates
            .list(templateFilter)
            .getItems()
            .stream()
            .filter(t -> !existingTemplates.contains(t.getShortCode()))
            .toList());
  }

  public void addTemplate() {
    MailTemplate result = selectedGlobalTemplate.duplicate();
    result.setSeason(seasonContext.getCurrentSeason());
    result = mailTemplates.save(result);

    try {
      FacesContext context = FacesContext.getCurrentInstance();
      context.getExternalContext().redirect(String.format(
              "../../settings/mailTemplates/editor.xhtml?templateId=%d",
              result.getId()));
      context.responseComplete();
    } catch (IOException ex) {
      Logger.getLogger(MailTemplateIndexView.class.getName()).log(
              Level.SEVERE,
              "Error redirecting to mail template.",
              ex);
    }
  }

  public void addAllTemplates() {
    for (MailTemplate t : selectableGlobalTemplates) {
      MailTemplate result = t.duplicate();
      result.setSeason(seasonContext.getCurrentSeason());
      mailTemplates.save(result);
    }
    
    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Vorlagen erstellt."));
    init();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the seasonContext
   */
  public SeasonContext getSeasonContext() {
    return seasonContext;
  }

  /**
   * @return the mailTemplateList
   */
  public List<MailTemplate> getMailTemplateList() {
    return mailTemplateList;
  }

  /**
   * @param mailTemplateList the mailTemplateList to set
   */
  public void setMailTemplateList(List<MailTemplate> mailTemplateList) {
    this.mailTemplateList = mailTemplateList;
  }

  /**
   * @return the selectedGlobalTemplate
   */
  public MailTemplate getSelectedGlobalTemplate() {
    return selectedGlobalTemplate;
  }

  /**
   * @param selectedGlobalTemplate the selectedGlobalTemplate to set
   */
  public void setSelectedGlobalTemplate(MailTemplate selectedGlobalTemplate) {
    this.selectedGlobalTemplate = selectedGlobalTemplate;
  }

  /**
   * @return the selectableGlobalTemplates
   */
  public List<MailTemplate> getSelectableGlobalTemplates() {
    return selectableGlobalTemplates;
  }

  /**
   * @param selectableGlobalTemplates the selectableGlobalTemplates to set
   */
  public void setSelectableGlobalTemplates(List<MailTemplate> selectableGlobalTemplates) {
    this.selectableGlobalTemplates = selectableGlobalTemplates;
  }
  //</editor-fold>
}
