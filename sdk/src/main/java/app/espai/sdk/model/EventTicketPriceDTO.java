package app.espai.sdk.model;

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
  //</editor-fold>
}
