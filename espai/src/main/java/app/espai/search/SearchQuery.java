
package app.espai.search;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rborowski
 */
public class SearchQuery {

  private String keyword;
  private List<SearchFacet> facets = new LinkedList<>();
  private int offset = 0;
  private int limit = 50;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the keyword
   */
  public String getKeyword() {
    return keyword;
  }

  /**
   * @param keyword the keyword to set
   */
  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  /**
   * @return the facets
   */
  public List<SearchFacet> getFacets() {
    return facets;
  }

  /**
   * @param facets the facets to set
   */
  public void setFacets(List<SearchFacet> facets) {
    this.facets = facets;
  }

  /**
   * @return the offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * @param offset the offset to set
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }

  /**
   * @return the limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * @param limit the limit to set
   */
  public void setLimit(int limit) {
    this.limit = limit;
  }
  //</editor-fold>

}
