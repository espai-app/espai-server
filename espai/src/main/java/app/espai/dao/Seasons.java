/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.filter.EventFilter;
import app.espai.filter.EventSerialFilter;
import app.espai.filter.MailTemplateFilter;
import app.espai.filter.SeasonPriceTemplateFilter;
import app.espai.filter.SeasonProductionFilter;
import app.espai.filter.SeasonVenueFilter;
import app.espai.model.Event;
import app.espai.model.EventSerial;
import app.espai.model.MailTemplate;
import app.espai.model.Season;
import app.espai.model.SeasonPriceTemplate;
import app.espai.model.SeasonProduction;
import app.espai.model.SeasonVenue;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;

/**
 * DAO class for season
 */
@Named
@Stateless
public class Seasons extends AbstractSeasons {
  
  @EJB
  private EventSerials eventSerials;
  
  @EJB
  private SeasonPriceTemplates priceTemplates;
  
  @EJB
  private SeasonVenues seasonVenues;
  
  @EJB
  private SeasonProductions seasonProductions;
  
  @EJB
  private MailTemplates mailTemplates;
  
  @EJB
  private Events events;
  
  public void delete(Season season) {
    
    EventSerialFilter serialFilter = new EventSerialFilter();
    serialFilter.setSeason(season);
    for (EventSerial s : eventSerials.list(serialFilter).getItems()) {
      eventSerials.delete(s);
    }
    
    SeasonPriceTemplateFilter priceFilter = new SeasonPriceTemplateFilter();
    priceFilter.setSeason(season);
    for (SeasonPriceTemplate t : priceTemplates.list(priceFilter).getItems()) {
      priceTemplates.delete(t);
    }
    
    SeasonVenueFilter venueFilter = new SeasonVenueFilter();
    venueFilter.setSeason(season);
    for (SeasonVenue v : seasonVenues.list(venueFilter).getItems()) {
      seasonVenues.delete(v);
    }
    
    SeasonProductionFilter productionFilter = new SeasonProductionFilter();
    productionFilter.setSeason(season);
    for (SeasonProduction p : seasonProductions.list(productionFilter).getItems()) {
      seasonProductions.delete(p);
    }
    
    MailTemplateFilter mailFilter = new MailTemplateFilter();
    mailFilter.setSeason(season);
    for (MailTemplate m : mailTemplates.list(mailFilter).getItems()) {
      mailTemplates.delete(m);
    }
    
    EventFilter eventFilter = new EventFilter();
    eventFilter.setSeason(season);
    for (Event e : events.list(eventFilter).getItems()) {
      events.delete(e);
    }
    
    super.delete(season);
  }

}