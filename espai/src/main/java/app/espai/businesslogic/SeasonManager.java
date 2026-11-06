/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.businesslogic;

import app.espai.dao.EventSerials;
import app.espai.dao.MailTemplates;
import app.espai.dao.SeasonPriceTemplates;
import app.espai.dao.Seasons;
import app.espai.filter.EventSerialFilter;
import app.espai.filter.MailTemplateFilter;
import app.espai.filter.SeasonPriceTemplateFilter;
import app.espai.model.EventSerial;
import app.espai.model.MailTemplate;
import app.espai.model.Season;
import app.espai.model.SeasonPriceTemplate;
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
public class SeasonManager {
  
  @EJB
  private Seasons seasons;

  @EJB
  private SeasonPriceTemplates priceTemplates;
  
  
  @EJB
  private EventSerials eventSerials;
  
  @EJB
  private MailTemplates mailTemplates;
  
  public Season createFromTemplate(Season newSeason, Season template) {
    
    newSeason.setMailAccount(template.getMailAccount());
    newSeason = seasons.save(newSeason);
    
    // duplicate price templates
    SeasonPriceTemplateFilter priceFilter = new SeasonPriceTemplateFilter();
    priceFilter.setSeason(template);
    List<SeasonPriceTemplate> sourcePrices = priceTemplates.list(priceFilter).getItems();
    
    for (SeasonPriceTemplate t : sourcePrices) {
      SeasonPriceTemplate n = new SeasonPriceTemplate();
      n.setSeason(newSeason);
      n.setPriceCategory(t.getPriceCategory());
      n.setPrice(t.getPrice());
      priceTemplates.save(n);
    }
    
    // duplicate serials
    EventSerialFilter serialFilter = new EventSerialFilter();
    serialFilter.setSeason(template);
    List<EventSerial> sourceSerials = eventSerials.list(serialFilter).getItems();
    
    for (EventSerial t : sourceSerials) {
      EventSerial n = new EventSerial();
      n.setSeason(newSeason);
      n.setName(t.getName());
      n.setDescription(t.getDescription());
      eventSerials.save(n);
    }
    
    // duplicate mail templates
    MailTemplateFilter mailFilter = new MailTemplateFilter();
    mailFilter.setSeason(template);
    List<MailTemplate> sourceMails = mailTemplates.list(mailFilter).getItems();
    for (MailTemplate t : sourceMails) {
      MailTemplate n = new MailTemplate();
      n.setSeason(newSeason);
      n.setDescription(t.getDescription());
      n.setHtmlMessage(t.getHtmlMessage());
      n.setName(t.getName());
      n.setReplyTo(t.getReplyTo());
      n.setSender(t.getSender());
      n.setShortCode(t.getShortCode());
      n.setSubject(t.getSubject());
      mailTemplates.save(n);
    }
    
    return newSeason;
  }
  
}
