package app.espai.sdk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "EventSerial",
        description = "Represents a serial of events.")
public class EventSerialDTO {

  private Long id;

  @NotNull(message = "name is a required field")
  @NotBlank(message = "name must not be blank")
  @Size(max = 150, message = "name must be between 0 to 100 characters")
  private String name;

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
