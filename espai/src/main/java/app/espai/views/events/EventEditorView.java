package app.espai.views.events;

import app.espai.dao.EventSerials;
import app.espai.dao.Events;
import app.espai.dao.Halls;
import app.espai.dao.Presenters;
import app.espai.dao.ProductionVersions;
import app.espai.dao.SeasonProductions;
import app.espai.dao.SeasonVenues;
import app.espai.filter.EventSerialFilter;
import app.espai.filter.HallFilter;
import app.espai.filter.ProductionVersionFilter;
import app.espai.model.Event;
import app.espai.model.EventSerial;
import app.espai.model.Hall;
import app.espai.model.Presenter;
import app.espai.model.Production;
import app.espai.model.ProductionVersion;
import app.espai.model.Season;
import app.espai.model.Venue;
import app.espai.views.SeasonContext;
import app.espai.views.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;
import rocks.xprs.runtime.exceptions.InvalidDataException;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class EventEditorView implements Serializable {

  @EJB
  private UserContext userContext;

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private Events events;

  @EJB
  private EventSerials eventSerials;

  @EJB
  private Halls halls;

  @EJB
  private Presenters presenters;

  @EJB
  private SeasonVenues seasonVenues;

  @EJB
  private SeasonProductions seasonProductions;

  @EJB
  private ProductionVersions productionVersions;

  private Event event;
  private Venue venue;
  private Production production;
  private List<EventSerial> eventSerialList;
  private List<Venue> venueList;
  private List<Hall> hallList;
  private List<? extends Presenter> presenterList;
  private List<Production> productionList;
  private List<ProductionVersion> productionVersionList;

  @PostConstruct
  public void init() {

    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String seasonIdParam = econtext.getRequestParameterMap().get("seasonId");
    if (seasonIdParam == null || !seasonIdParam.matches("\\d+")) {
      throw new InvalidDataException("seasonId missing.");
    }
    Season currentSeason = seasonContext.getCurrentSeason();
    if (currentSeason == null) {
      throw new InvalidDataException("seasonId missing.");
    }

    EventSerialFilter eventSerialFilter = new EventSerialFilter();
    eventSerialFilter.setSeason(currentSeason);
    eventSerialList = eventSerials.list(eventSerialFilter).getItems();

    venueList = userContext.isRestricted()
            ? userContext.getVenues()
            : seasonVenues.getVenues(currentSeason);

    presenterList = presenters.list().getItems();
    presenterList.sort(Presenters.DEFAULT_ORDER);

    productionList = seasonProductions.getProductions(currentSeason);

    String eventIdParam = econtext.getRequestParameterMap().get("eventId");
    if (eventIdParam == null) {
      event = new Event();
      event.setSeason(currentSeason);

      String parentEventIdParam = econtext.getRequestParameterMap().get("parentEventId");
      if (parentEventIdParam != null && parentEventIdParam.matches("\\d+")) {
        Event parentEvent = events.get(Long.parseLong(parentEventIdParam));
        event.setParentEvent(parentEvent);
        event.setDate(parentEvent.getDate());
        event.setTime(parentEvent.getTime());
        event.setHall(parentEvent.getHall());

        venue = parentEvent.getHall().getVenue();
        production = productionList.get(0);
        onVenueChanged();
        onProductionChanged();
      }
    } else {
      event = events.get(Long.parseLong(eventIdParam));
      venue = event.getHall() != null ? event.getHall().getVenue() : null;
      onVenueChanged();
      production = event.getProduction() != null ? event.getProduction().getProduction() : null;
      onProductionChanged();
    }
  }

  public void save() {
    events.save(event);
    PrimeFaces.current().dialog().closeDynamic(event);
  }

  public void onVenueChanged() {
    if (venue == null) {
      return;
    }

    HallFilter hallFilter = new HallFilter();
    hallFilter.setVenue(venue);

    hallList = halls.list(hallFilter).getItems();
    hallList.sort(Halls.DEFAULT_ORDER);
  }

  public void onProductionChanged() {
    if (production == null) {
      return;
    }

    ProductionVersionFilter versionFilter = new ProductionVersionFilter();
    versionFilter.setProduction(production);

    productionVersionList = productionVersions.list(versionFilter).getItems();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the event
   */
  public Event getEvent() {
    return event;
  }

  /**
   * @param event the event to set
   */
  public void setEvent(Event event) {
    this.event = event;
  }

  /**
   * @return the venue
   */
  public Venue getVenue() {
    return venue;
  }

  /**
   * @param venue the venue to set
   */
  public void setVenue(Venue venue) {
    this.venue = venue;
  }

  /**
   * @return the production
   */
  public Production getProduction() {
    return production;
  }

  /**
   * @param production the production to set
   */
  public void setProduction(Production production) {
    this.production = production;
  }

  /**
   * @return the eventSerialList
   */
  public List<EventSerial> getEventSerialList() {
    return eventSerialList;
  }

  /**
   * @param eventSerialList the eventSerialList to set
   */
  public void setEventSerialList(List<EventSerial> eventSerialList) {
    this.eventSerialList = eventSerialList;
  }

  /**
   * @return the venueList
   */
  public List<Venue> getVenueList() {
    return venueList;
  }

  /**
   * @param venueList the venueList to set
   */
  public void setVenueList(List<Venue> venueList) {
    this.venueList = venueList;
  }

  /**
   * @return the hallList
   */
  public List<Hall> getHallList() {
    return hallList;
  }

  /**
   * @param hallList the hallList to set
   */
  public void setHallList(List<Hall> hallList) {
    this.hallList = hallList;
  }

  /**
   * @return the presenterList
   */
  public List<? extends Presenter> getPresenterList() {
    return presenterList;
  }

  /**
   * @param presenterList the presenterList to set
   */
  public void setPresenterList(List<? extends Presenter> presenterList) {
    this.presenterList = presenterList;
  }

  /**
   * @return the productionList
   */
  public List<Production> getProductionList() {
    return productionList;
  }

  /**
   * @param productionList the productionList to set
   */
  public void setProductionList(List<Production> productionList) {
    this.productionList = productionList;
  }

  /**
   * @return the productionVersionList
   */
  public List<ProductionVersion> getProductionVersionList() {
    return productionVersionList;
  }

  /**
   * @param productionVersionList the productionVersionList to set
   */
  public void setProductionVersionList(List<ProductionVersion> productionVersionList) {
    this.productionVersionList = productionVersionList;
  }
  //</editor-fold>

}
