
package app.espai.search;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rborowski
 */
public class SearchFacet {

  private String name;
  private String label;
  private List<SearchFacetValue> available = new LinkedList<>();
  private List<String> selected = new LinkedList<>();

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
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * @return the available
   */
  public List<SearchFacetValue> getAvailable() {
    return available;
  }

  /**
   * @param available the available to set
   */
  public void setAvailable(List<SearchFacetValue> available) {
    this.available = available;
  }

  /**
   * @return the selected
   */
  public List<String> getSelected() {
    return selected;
  }

  /**
   * @param selected the selected to set
   */
  public void setSelected(List<String> selected) {
    this.selected = selected;
  }
  //</editor-fold>

}
