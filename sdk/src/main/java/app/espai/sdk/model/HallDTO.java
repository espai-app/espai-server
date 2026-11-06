
package app.espai.sdk.model;

import java.util.List;

/**
 *
 * @author rborowski
 */
public class HallDTO {

  private Long id;
  private VenueDTO venue;
  private String name;
  private List<HallCapacityDTO> hallCapacities;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
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
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the hallCapacities
   */
  public List<HallCapacityDTO> getHallCapacities() {
    return hallCapacities;
  }

  /**
   * @param hallCapacities the hallCapacities to set
   */
  public void setHallCapacities(List<HallCapacityDTO> hallCapacities) {
    this.hallCapacities = hallCapacities;
  }
  //</editor-fold>

}
