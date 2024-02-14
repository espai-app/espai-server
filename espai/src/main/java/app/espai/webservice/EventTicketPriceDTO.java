package app.espai.webservice;

import app.espai.model.EventTicketPrice;
import java.math.BigDecimal;

/**
 *
 * @author rborowski
 */
public class EventTicketPriceDTO {

  private Long id;
  private String name;
  private String description;
  private Integer price;

  public static EventTicketPriceDTO of(EventTicketPrice price) {
    EventTicketPriceDTO result = new EventTicketPriceDTO();
    result.setId(price.getId());
    result.setName(price.getPriceCategory().getName());
    result.setDescription(price.getPriceCategory().getDescription());
    result.setPrice(price.getPrice().getAmount().multiply(BigDecimal.valueOf(100)).intValue());
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
  //</editor-fold>
}
