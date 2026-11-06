
package app.espai.sdk.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "Agency",
        description = "Represents a license owner.")
public class AgencyDTO {

  private Long id;

  @NotNull
  @NotBlank
  @Size(max = 200, message = "name must be between 0 to 200 characters")
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

  @Override
  public boolean equals(Object o) {
    if (o instanceof AgencyDTO a) {
      return Objects.equals(name, a.getName());
    }

    return false;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 13 * hash + Objects.hashCode(this.name);
    return hash;
  }
}
