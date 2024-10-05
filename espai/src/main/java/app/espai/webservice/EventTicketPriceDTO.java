package app.espai.webservice;

import app.espai.model.EventTicketPrice;
import java.math.BigDecimal;

/**
 *
 * @author rborowski
 */
public class EventTicketPriceDTO {

  private Long id;
  private String seatCategory;
  private String priceCategory;
  private String description;
  private Integer price;
  private String currency;
  private Integer seatsAvailable;

  public static EventTicketPriceDTO of(EventTicketPrice price, Integer seatsAvailable) {
    EventTicketPriceDTO result = new EventTicketPriceDTO();
    result.setId(price.getId());
    result.setSeatCategory(price.getSeatCategory().getName());
    result.setPriceCategory(price.getPriceCategory().getName());
    result.setDescription(price.getPriceCategory().getDescription());
    result.setPrice(price.getPrice().getAmount().multiply(BigDecimal.valueOf(100)).intValue());
    result.setCurrency(price.getPrice().getCurrency());
    result.setSeatsAvailable(seatsAvailable);
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
   * @return the seatCategory
   */
  public String getSeatCategory() {
    return seatCategory;
  }

  /**
   * @param seatCategory the seatCategory to set
   */
  public void setSeatCategory(String seatCategory) {
    this.seatCategory = seatCategory;
  }

  /**
   * @return the priceCategory
   */
  public String getPriceCategory() {
    return priceCategory;
  }

  /**
   * @param priceCategory the priceCategory to set
   */
  public void setPriceCategory(String priceCategory) {
    this.priceCategory = priceCategory;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the price
   */
  public Integer getPrice() {
    return price;
  }

  /**
   * @param price the price to set
   */
  public void setPrice(Integer price) {
    this.price = price;
  }

  /**
   * @return the currency
   */
  public String getCurrency() {
    return currency;
  }

  /**
   * @param currency the currency to set
   */
  public void setCurrency(String currency) {
    this.currency = currency;
  }

  /**
   * @return the seatsAvailable
   */
  public Integer getSeatsAvailable() {
    return seatsAvailable;
  }

  /**
   * @param seatsAvailable the seatsAvailable to set
   */
  public void setSeatsAvailable(Integer seatsAvailable) {
    this.seatsAvailable = seatsAvailable;
  }
  //</editor-fold>
}
