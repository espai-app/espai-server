
package app.espai.sdk.model;

import java.time.LocalDate;

/**
 *
 * @author rborowski
 */
public class SeasonDTO {

  private Long id;
  private String name;
  private LocalDate start;
  private LocalDate end;

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

  /**
   * @return the start
   */
  public LocalDate getStart() {
    return start;
  }

  /**
   * @param start the start to set
   */
  public void setStart(LocalDate start) {
    this.start = start;
  }

  /**
   * @return the end
   */
  public LocalDate getEnd() {
    return end;
  }

  /**
   * @param end the end to set
   */
  public void setEnd(LocalDate end) {
    this.end = end;
  }
  //</editor-fold>
  
}
