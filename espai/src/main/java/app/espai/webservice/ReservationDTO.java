package app.espai.webservice;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rborowski
 */
public class ReservationDTO implements Serializable {

  private String company;
  private String givenName;
  private String surname;
  private String address;
  private String postcode;
  private String city;
  private String phone;
  private String email;

  private String message;

  private Long event;
  private List<Long> childEvents = new LinkedList<>();
  private Map<Long, Integer> tickets = new HashMap<>();

  private Map<String, String> extras = new HashMap<>();
  private boolean acceptTos;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the company
   */
  public String getCompany() {
    return company;
  }

  /**
   * @param company the company to set
   */
  public void setCompany(String company) {
    this.company = company;
  }

  /**
   * @return the givenName
   */
  public String getGivenName() {
    return givenName;
  }

  /**
   * @param givenName the givenName to set
   */
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  /**
   * @return the surname
   */
  public String getSurname() {
    return surname;
  }

  /**
   * @param surname the surname to set
   */
  public void setSurname(String surname) {
    this.surname = surname;
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
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return the event
   */
  public Long getEvent() {
    return event;
  }

  /**
   * @param event the event to set
   */
  public void setEvent(Long event) {
    this.event = event;
  }

  /**
   * @return the childEvents
   */
  public List<Long> getChildEvents() {
    return childEvents;
  }

  /**
   * @param childEvents the childEvents to set
   */
  public void setChildEvents(List<Long> childEvents) {
    this.childEvents = childEvents;
  }

  /**
   * @return the tickets
   */
  public Map<Long, Integer> getTickets() {
    return tickets;
  }

  /**
   * @param tickets the tickets to set
   */
  public void setTickets(Map<Long, Integer> tickets) {
    this.tickets = tickets;
  }

  /**
   * @return the extras
   */
  public Map<String, String> getExtras() {
    return extras;
  }

  /**
   * @param extras the extras to set
   */
  public void setExtras(Map<String, String> extras) {
    this.extras = extras;
  }

  /**
   * @return the acceptTos
   */
  public boolean isAcceptTos() {
    return acceptTos;
  }

  /**
   * @param acceptTos the acceptTos to set
   */
  public void setAcceptTos(boolean acceptTos) {
    this.acceptTos = acceptTos;
  }
  //</editor-fold>

}
