
package app.espai.sdk.model;

/**
 *
 * @author rborowski
 */
public class HallCapacityDTO {

  private Long seatCategoryId;
  private String seatCategoryName;
  private int capacity;

  /**
   * @return the seatCategoryId
   */
  public Long getSeatCategoryId() {
    return seatCategoryId;
  }

  /**
   * @param seatCategoryId the seatCategoryId to set
   */
  public void setSeatCategoryId(Long seatCategoryId) {
    this.seatCategoryId = seatCategoryId;
  }

  /**
   * @return the seatCategoryName
   */
  public String getSeatCategoryName() {
    return seatCategoryName;
  }

  /**
   * @param seatCategoryName the seatCategoryName to set
   */
  public void setSeatCategoryName(String seatCategoryName) {
    this.seatCategoryName = seatCategoryName;
  }

  /**
   * @return the capacity
   */
  public int getCapacity() {
    return capacity;
  }

  /**
   * @param capacity the capacity to set
   */
  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

}
