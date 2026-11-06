package app.espai.sdk.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "Reservation",
        description = "Represents a reservation.")
public class ReservationDTO implements Serializable {

  @Size(max = 150, message = "company must be between 0 to 150 characters")
  private String company;

  @Size(max = 50, message = "givenName must be between 0 to 50 characters")
  private String givenName;

  @Size(max = 50, message = "surname must be between 0 to 50 characters")
  private String surname;

  @Size(max = 300, message = "address must be between 0 to 300 characters")
  private String address;

  @Size(max = 10, message = "postcode must be between 0 to 10 characters")
  private String postcode;

  @Size(max = 200, message = "city must be between 0 to 200 characters")
  private String city;

  @Size(max = 50, message = "phone must be between 0 to 50 characters")
  private String phone;

  @Size(max = 100, message = "email must be between 0 to 100 characters")
  private String email;

  @Size(max = 2000, message = "message must be between 0 to 2000 characters")
  private String message;

  @NotNull(message = "event must not be null")
  private Long event;
  private List<Long> childEvents = new LinkedList<>();

  @NotNull(message = "tickets must not be null")
  private Map<Long, Integer> tickets = new HashMap<>();

  private Map<String, String> extras = new HashMap<>();

  @AssertTrue(message = "Terms of Service mus be accepted.")
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
