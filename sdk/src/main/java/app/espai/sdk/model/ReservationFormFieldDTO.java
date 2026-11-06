/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.sdk.model;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "ReservationFormField",
        description = "Represents an extra input field of a reservation.")
public class ReservationFormFieldDTO {

  private String label;
  private String name;
  private String type;
  private List<String> presets;
  private boolean required;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
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
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return the presets
   */
  public List<String> getPresets() {
    return presets;
  }

  /**
   * @param presets the presets to set
   */
  public void setPresets(List<String> presets) {
    this.presets = presets;
  }

  /**
   * @return the required
   */
  public boolean isRequired() {
    return required;
  }

  /**
   * @param required the required to set
   */
  public void setRequired(boolean required) {
    this.required = required;
  }
  //</editor-fold>

}
