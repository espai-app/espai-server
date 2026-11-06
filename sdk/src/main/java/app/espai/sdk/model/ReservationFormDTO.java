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
@Schema(name = "ReservationForm",
        description = "Represents the additional fields (extras) of a reservation form.")
public class ReservationFormDTO {

  private String name;
  private String termsConditionsUrl;
  private String privacyUrl;
  private List<ReservationFormFieldDTO> fields;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
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
   * @return the termsConditionsUrl
   */
  public String getTermsConditionsUrl() {
    return termsConditionsUrl;
  }

  /**
   * @param termsConditionsUrl the termsConditionsUrl to set
   */
  public void setTermsConditionsUrl(String termsConditionsUrl) {
    this.termsConditionsUrl = termsConditionsUrl;
  }

  /**
   * @return the privacyUrl
   */
  public String getPrivacyUrl() {
    return privacyUrl;
  }

  /**
   * @param privacyUrl the privacyUrl to set
   */
  public void setPrivacyUrl(String privacyUrl) {
    this.privacyUrl = privacyUrl;
  }

  /**
   * @return the fields
   */
  public List<ReservationFormFieldDTO> getFields() {
    return fields;
  }

  /**
   * @param fields the fields to set
   */
  public void setFields(List<ReservationFormFieldDTO> fields) {
    this.fields = fields;
  }
  //</editor-fold>

}
