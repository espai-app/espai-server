/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */
package app.espai.dao;

import app.espai.filter.MailTemplateFilter;
import app.espai.model.MailTemplate;
import app.espai.model.Season;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import java.util.List;

/**
 * DAO class for mailTemplate
 */
@Named
@Stateless
public class MailTemplates extends AbstractMailTemplates {

  public MailTemplate getByCode(String shortCode, Season season) {

    MailTemplateFilter mailTemplateFilter = new MailTemplateFilter();
    mailTemplateFilter.setShortCode(shortCode);
    mailTemplateFilter.setSeason(season);

    List<MailTemplate> templateList = list(mailTemplateFilter).getItems();
    if (templateList.isEmpty()) {
      mailTemplateFilter.setSeasonIsNull(true);
      templateList = list(mailTemplateFilter).getItems();
    }

    return templateList.get(0);
  }

}
