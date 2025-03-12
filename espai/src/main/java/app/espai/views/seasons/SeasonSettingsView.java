package app.espai.views.seasons;

import app.espai.dao.EventSerials;
import app.espai.dao.EventTicketPrices;
import app.espai.dao.Events;
import app.espai.dao.HallCapacities;
import app.espai.dao.MailAccounts;
import app.espai.dao.SeasonPriceTemplates;
import app.espai.dao.Seasons;
import app.espai.filter.EventFilter;
import app.espai.filter.EventTicketPriceFilter;
import app.espai.filter.HallCapacityFilter;
import app.espai.filter.SeasonPriceTemplateFilter;
import app.espai.model.Event;
import app.espai.model.EventSerial;
import app.espai.model.EventTicketPrice;
import app.espai.model.Hall;
import app.espai.model.HallCapacity;
import app.espai.model.MailAccount;
import app.espai.model.Season;
import app.espai.model.SeasonPriceTemplate;
import app.espai.model.SeatCategory;
import app.espai.views.Dialog;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class SeasonSettingsView {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private Seasons seasons;

  @EJB
  private MailAccounts mailAccounts;

  @EJB
  private SeasonPriceTemplates priceTemplates;
  
  @EJB
  private Events events;
  
  @EJB
  private HallCapacities hallCapacities;
  
  @EJB
  private EventTicketPrices ticketPrices;
  
  @EJB
  private EventSerials eventSerials;

  private Season currentSeason;
  private List<? extends MailAccount> mailAccountList;
  private List<SeasonPriceTemplate> priceTemplateList;
  private List<EventSerial> eventSerialList;

  @PostConstruct
  public void init() {
    currentSeason = seasonContext.getCurrentSeason();
    mailAccountList =mailAccounts.list().getItems();
    
    SeasonPriceTemplateFilter priceTemplateFilter = new SeasonPriceTemplateFilter();
    priceTemplateFilter.setSeason(currentSeason);
    priceTemplateList = priceTemplates.list(priceTemplateFilter).getItems();
  }

  public void save() {
    seasons.save(getCurrentSeason());

    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Änderungen gespeichert."));
  }
  
  public void addPriceTemplate() {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("seasonId", List.of(String.valueOf(seasonContext.getCurrentSeason().getId())));
    
    PrimeFaces.current().dialog().openDynamic("priceTemplateEditor", 
            Dialog.getDefaultOptions(500, 400), params);
  }
  
  public void editPriceTemplate(SeasonPriceTemplate template) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("priceTemplateId", List.of(String.valueOf(template.getId())));
    
    PrimeFaces.current().dialog().openDynamic("priceTemplateEditor", 
            Dialog.getDefaultOptions(500, 400), params);
  }

  public void deletePriceTemplate(SeasonPriceTemplate template) {
    priceTemplates.delete(template);
    init();
  }
  
  public void applyPriceTemplates() {
    SeasonPriceTemplateFilter seasonPriceTemplateFilter = new SeasonPriceTemplateFilter();
    seasonPriceTemplateFilter.setSeason(currentSeason);
    List<SeasonPriceTemplate> seasonPriceTemplateList = 
            priceTemplates.list(seasonPriceTemplateFilter).getItems();
    
    EventFilter eventFilter = new EventFilter();
    eventFilter.setSeason(currentSeason);
    
    List<Event> allEvents = events.list(eventFilter).getItems();
    List<Hall> allHalls = allEvents.stream().map(Event::getHall).distinct().toList();
    
    EventTicketPriceFilter priceFilter = new EventTicketPriceFilter();
    priceFilter.setEvents(allEvents);
    Map<Long, List<EventTicketPrice>> allPrices = ticketPrices.list(priceFilter)
            .getItems()
            .stream()
            .collect(Collectors.groupingBy(p -> p.getEvent().getId()));
    
    HallCapacityFilter capacityFilter = new HallCapacityFilter();
    capacityFilter.setHalls(allHalls);
    Map<Long, List<SeatCategory>> seatCategoryMap = hallCapacities.list(capacityFilter).getItems()
            .stream()
            .collect(Collectors.groupingBy(
                    h -> h.getHall().getId(), 
                    Collectors.mapping(HallCapacity::getSeatCategory, Collectors.toList())));

    for (Event currentEvent : allEvents) {
      
      List<EventTicketPrice> currentTicketPrices = allPrices.containsKey(currentEvent.getId()) 
              ? allPrices.get(currentEvent.getId()) 
              : Collections.EMPTY_LIST;
      List<SeatCategory> currentSeatCategories = seatCategoryMap.containsKey(currentEvent.getHall().getId()) 
              ? seatCategoryMap.get(currentEvent.getHall().getId()) 
              : Collections.EMPTY_LIST;
      
      // update or create ticket prices
      for (int i = 0; i < seasonPriceTemplateList.size(); i++) {
        SeasonPriceTemplate currentPriceTemplate = seasonPriceTemplateList.get(i);
        
        for (int j = 0; j < currentSeatCategories.size(); j++) {
          int currentIndex = i * (currentSeatCategories.size()) + j;
          SeatCategory currentSeatCategory = currentSeatCategories.get(j);
          EventTicketPrice currentPrice;
          
          if (currentTicketPrices.size() > currentIndex) {
            currentPrice = currentTicketPrices.get(currentIndex);
          } else {
            currentPrice = new EventTicketPrice();
            currentPrice.setEvent(currentEvent);
          }
          
          currentPrice.setPriceCategory(currentPriceTemplate.getPriceCategory());
          currentPrice.setSeatCategory(currentSeatCategory);
          currentPrice.setPrice(currentPriceTemplate.getPrice());
          
          ticketPrices.save(currentPrice);
        }
      }
      
      // if existing price list exceeds updated price list, delete items that are too much
      int maxIndex = (seasonPriceTemplateList.size() * currentSeatCategories.size());
      for (int i = maxIndex; i < currentTicketPrices.size(); i++) {
        ticketPrices.delete(currentTicketPrices.get(i));
      }
    }
    
    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Preise aktualisiert."));
  }
  
  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">

  /**
   * @return the currentSeason
   */

  public Season getCurrentSeason() {
    return currentSeason;
  }

  /**
   * @param currentSeason the currentSeason to set
   */
  public void setCurrentSeason(Season currentSeason) {
    this.currentSeason = currentSeason;
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

  /**
   * @return the priceTemplateList
   */
  public List<SeasonPriceTemplate> getPriceTemplateList() {
    return priceTemplateList;
  }

  /**
   * @param priceTemplateList the priceTemplateList to set
   */
  public void setPriceTemplateList(List<SeasonPriceTemplate> priceTemplateList) {
    this.priceTemplateList = priceTemplateList;
  }
  //</editor-fold>
  
}
