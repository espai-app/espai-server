
package app.espai.sdk.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "EventRequest",
        description = "Represents an event request.")
public class EventRequestDTO extends ReservationDTO {

  @NotNull
  private LocalDate date;

  @NotNull
  private LocalTime time;

  @NotNull
  private Long productionVersionId;

  @NotNull
  private Long hallId;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
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
   * @return the productionVersionId
   */
  public Long getProductionVersionId() {
    return productionVersionId;
  }

  /**
   * @param productionVersionId the productionVersionId to set
   */
  public void setProductionVersionId(Long productionVersionId) {
    this.productionVersionId = productionVersionId;
  }

  /**
   * @return the hallId
   */
  public Long getHallId() {
    return hallId;
  }

  /**
   * @param hallId the hallId to set
   */
  public void setHallId(Long hallId) {
    this.hallId = hallId;
  }
  //</editor-fold>
}
