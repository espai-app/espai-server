
package app.espai.search;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rborowski
 */
public class SearchResult<T> {

  private SearchQuery searchQuery;
  private List<T> items;
  private long total;

  public long getCurrentPage() {
    return Math.ceilDiv(searchQuery.getOffset(), searchQuery.getLimit());
  }

  public void setCurrentPage(long pageNumber) {
    searchQuery.setOffset((int) (pageNumber * searchQuery.getLimit()));
  }

  public List<Long> getPages() {
    long numberOfPages = Math.ceilDiv(total, searchQuery.getLimit());
    List<Long> result = new LinkedList();

    for (long i = 0; i < numberOfPages; i++) {
      result.add(i);
    }
    return result;
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the searchQuery
   */
  public SearchQuery getSearchQuery() {
    return searchQuery;
  }

  /**
   * @param searchQuery the searchQuery to set
   */
  public void setSearchQuery(SearchQuery searchQuery) {
    this.searchQuery = searchQuery;
  }

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
  public long getTotal() {
    return total;
  }

  /**
   * @param total the total to set
   */
  public void setTotal(long total) {
    this.total = total;
  }
  //</editor-fold>

}
