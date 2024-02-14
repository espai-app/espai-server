package app.espai.views.mailTemplates;

import app.espai.dao.MailTemplates;
import app.espai.filter.MailTemplateFilter;
import app.espai.model.MailTemplate;
import app.espai.model.Season;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Named
@Stateful
@RequestScoped
@RolesAllowed("MANAGER")
public class MailTemplateIndexView {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private MailTemplates mailTemplates;

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
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
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
  //</editor-fold>

}
