/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.sdk.model;

/**
 *
 * @author rborowski
 */
public class SeasonVenueDTO {

  private Long id;
  private String name;
  private String address;
  private String postcode;
  private String city;
  private String phone;
  private String email;
  private String publicNotes;
  private String seasonNotes;

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
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address the address to set
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * @return the postcode
   */
  public String getPostcode() {
    return postcode;
  }

  /**
   * @param postcode the postcode to set
   */
  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  /**
   * @return the city
   */
  public String getCity() {
    return city;
  }

  /**
   * @param city the city to set
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * @return the phone
   */
  public String getPhone() {
    return phone;
  }

  /**
   * @param phone the phone to set
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return the publicNotes
   */
  public String getPublicNotes() {
    return publicNotes;
  }

  /**
   * @param publicNotes the publicNotes to set
   */
  public void setPublicNotes(String publicNotes) {
    this.publicNotes = publicNotes;
  }

  /**
   * @return the seasonNotes
   */
  public String getSeasonNotes() {
    return seasonNotes;
  }

  /**
   * @param seasonNotes the seasonNotes to set
   */
  public void setSeasonNotes(String seasonNotes) {
    this.seasonNotes = seasonNotes;
  }
  //</editor-fold>

}
