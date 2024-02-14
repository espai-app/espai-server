package app.espai.webservice;

import app.espai.model.EventSerial;

/**
 *
 * @author rborowski
 */
public class EventSerialDTO {

  private Long id;
  private String name;

  public static EventSerialDTO of(EventSerial serial) {
    EventSerialDTO result = new EventSerialDTO();
    result.setId(serial.getId());
    result.setName(serial.getName());
    return result;
  }

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
  //</editor-fold>
}