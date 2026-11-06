package app.espai.sdk.model;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "ListResult",
        description = "Represents a List of results of a certain type.")
public class ListResult<T> {

  private List<T> items;

  @Schema(description = "Total number of results.")
  private int total;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the items
   */
  public List<T> getItems() {
    return items;
  }

  /**
   * @param items the items to set
   */
  public void setItems(List<T> items) {
    this.items = items;
  }

  /**
   * @return the total
   */
  public int getTotal() {
    return total;
  }

  /**
   * @param total the total to set
   */
  public void setTotal(int total) {
    this.total = total;
  }
  //</editor-fold>
}
