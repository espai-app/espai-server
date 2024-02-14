package app.espai.model;

import java.io.Serializable;

/**
 *
 * @author rborowski
 */
public class VenueEventStatistic implements Serializable {

  private long venueId;
  private String venueName;
  private String venueCity;
  private long countEvents;

  public VenueEventStatistic() {

  }

  public VenueEventStatistic(long venueId, String venueName, String venueCity, long countEvents) {
    this.venueId = venueId;
    this.venueName = venueName;
    this.venueCity = venueCity;
    this.countEvents = countEvents;
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the venueId
   */
  public long getVenueId() {
    return venueId;
  }

  /**
   * @param venueId the venueId to set
   */
  public void setVenueId(long venueId) {
    this.venueId = venueId;
  }

  /**
   * @return the venueName
   */
  public String getVenueName() {
    return venueName;
  }

  /**
   * @param venueName the venueName to set
   */
  public void setVenueName(String venueName) {
    this.venueName = venueName;
  }

  /**
   * @return the venueCity
   */
  public String getVenueCity() {
    return venueCity;
  }

  /**
   * @param venueCity the venueCity to set
   */
  public void setVenueCity(String venueCity) {
    this.venueCity = venueCity;
  }

  /**
   * @return the countEvents
   */
  public long getCountEvents() {
    return countEvents;
  }

  /**
   * @param countEvents the countEvents to set
   */
  public void setCountEvents(long countEvents) {
    this.countEvents = countEvents;
  }
  //</editor-fold>

}
