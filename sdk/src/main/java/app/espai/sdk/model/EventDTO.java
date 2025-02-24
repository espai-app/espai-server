package app.espai.sdk.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rborowski
 */
public class EventDTO {

  private Long id;
  private LocalDate date;
  private LocalTime time;
  private String title;
  private String version;
  private ProductionDTO production;
  private VenueDTO venue;
  private PresenterDTO host;
  private PresenterDTO coHost;
  private Map<String, Integer> availableTickets = new HashMap<>();
  private Integer ticketLimit;
  private boolean mandatory;
  private boolean reservable;
  private EventSerialDTO eventSerial;
  private List<EventDTO> childEvents;
  private List<EventTicketPriceDTO> prices;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the eventId to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the date
   */
  public LocalDate getDate() {
    return date;
  }

  /**
   * @param date the date to set
   */
  public void setDate(LocalDate date) {
    this.date = date;
  }

  /**
   * @return the time
   */
  public LocalTime getTime() {
    return time;
  }

  /**
   * @param time the time to set
   */
  public void setTime(LocalTime time) {
    this.time = time;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @return the production
   */
  public ProductionDTO getProduction() {
    return production;
  }

  /**
   * @param production the production to set
   */
  public void setProduction(ProductionDTO production) {
    this.production = production;
  }

  /**
   * @return the venue
   */
  public VenueDTO getVenue() {
    return venue;
  }

  /**
   * @param venue the venue to set
   */
  public void setVenue(VenueDTO venue) {
    this.venue = venue;
  }

  /**
   * @return the host
   */
  public PresenterDTO getHost() {
    return host;
  }

  /**
   * @param host the host to set
   */
  public void setHost(PresenterDTO host) {
    this.host = host;
  }

  /**
   * @return the coHost
   */
  public PresenterDTO getCoHost() {
    return coHost;
  }

  /**
   * @param coHost the coHost to set
   */
  public void setCoHost(PresenterDTO coHost) {
    this.coHost = coHost;
  }

  /**
   * @return the availableTickets
   */
  public Map<String, Integer> getAvailableTickets() {
    return availableTickets;
  }

  /**
   * @param availableTickets the availableTickets to set
   */
  public void setAvailableTickets(Map<String, Integer> availableTickets) {
    this.availableTickets = availableTickets;
  }

  /**
   * @return the ticketLimit
   */
  public Integer getTicketLimit() {
    return ticketLimit;
  }

  /**
   * @param ticketLimit the ticketLimit to set
   */
  public void setTicketLimit(Integer ticketLimit) {
    this.ticketLimit = ticketLimit;
  }

  /**
   * @return the mandatory
   */
  public boolean isMandatory() {
    return mandatory;
  }

  /**
   * @param mandatory the mandatory to set
   */
  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }

  /**
   * @return the reservable
   */
  public boolean isReservable() {
    return reservable;
  }

  /**
   * @param reservable the reservable to set
   */
  public void setReservable(boolean reservable) {
    this.reservable = reservable;
  }

  /**
   * @return the eventSerial
   */
  public EventSerialDTO getEventSerial() {
    return eventSerial;
  }

  /**
   * @param eventSerial the eventSerial to set
   */
  public void setEventSerial(EventSerialDTO eventSerial) {
    this.eventSerial = eventSerial;
  }

  /**
   * @return the childEvents
   */
  public List<EventDTO> getChildEvents() {
    return childEvents;
  }

  /**
   * @param childEvents the childEvents to set
   */
  public void setChildEvents(List<EventDTO> childEvents) {
    this.childEvents = childEvents;
  }

  /**
   * @return the prices
   */
  public List<EventTicketPriceDTO> getPrices() {
    return prices;
  }

  /**
   * @param prices the prices to set
   */
  public void setPrices(List<EventTicketPriceDTO> prices) {
    this.prices = prices;
  }
  //</editor-fold>

}
