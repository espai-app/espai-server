
package app.espai.sdk.model;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "LicenseCondition",
        description = "Represents the conditions under wich the presentation can be licensed.")
public class LicenseConditionDTO {

  private Long id;
  private AgencyDTO agency;
  private boolean commercial;
  private BigDecimal guarantee;
  private BigDecimal fixedPrice;
  private BigDecimal ticketShare;
  private BigDecimal additionalCost;
  private String currency;
  private String notes;

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
   * @return the agency
   */
  public AgencyDTO getAgency() {
    return agency;
  }

  /**
   * @param agency the agency to set
   */
  public void setAgency(AgencyDTO agency) {
    this.agency = agency;
  }

  /**
   * @return the commercial
   */
  public boolean isCommercial() {
    return commercial;
  }

  /**
   * @param commercial the commercial to set
   */
  public void setCommercial(boolean commercial) {
    this.commercial = commercial;
  }

  /**
   * @return the guarantee
   */
  public BigDecimal getGuarantee() {
    return guarantee;
  }

  /**
   * @param guarantee the guarantee to set
   */
  public void setGuarantee(BigDecimal guarantee) {
    this.guarantee = guarantee;
  }

  /**
   * @return the fixedPrice
   */
  public BigDecimal getFixedPrice() {
    return fixedPrice;
  }

  /**
   * @param fixedPrice the fixedPrice to set
   */
  public void setFixedPrice(BigDecimal fixedPrice) {
    this.fixedPrice = fixedPrice;
  }

  /**
   * @return the ticketShare
   */
  public BigDecimal getTicketShare() {
    return ticketShare;
  }

  /**
   * @param ticketShare the ticketShare to set
   */
  public void setTicketShare(BigDecimal ticketShare) {
    this.ticketShare = ticketShare;
  }

  /**
   * @return the additionalCost
   */
  public BigDecimal getAdditionalCost() {
    return additionalCost;
  }

  /**
   * @param additionalCost the additionalCost to set
   */
  public void setAdditionalCost(BigDecimal additionalCost) {
    this.additionalCost = additionalCost;
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
   * @return the notes
   */
  public String getNotes() {
    return notes;
  }

  /**
   * @param notes the notes to set
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }
  //</editor-fold>

}
