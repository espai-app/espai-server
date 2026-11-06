package app.espai.views.productions;

import app.espai.dao.ProductionMetaFields;
import app.espai.dao.ProductionTypes;
import app.espai.filter.ProductionMetaFieldFilter;
import app.espai.filter.ProductionTypeFilter;
import app.espai.model.Production;
import app.espai.model.ProductionMetaField;
import app.espai.model.ProductionType;
import app.espai.search.ProductionIndexManager;
import app.espai.search.SearchFacet;
import app.espai.search.SearchQuery;
import app.espai.search.SearchResult;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class ProductionIndexView implements Serializable {

  @EJB
  private ProductionIndexManager searchManager;

  @EJB
  private ProductionTypes productionTypes;

  @EJB
  private ProductionMetaFields metaFields;

  private SearchQuery searchQuery;
  private SearchResult<Production> searchResult;
  private List<ProductionType> productionTypeList;

  @PostConstruct
  public void init() {

    ProductionTypeFilter typeFilter = new ProductionTypeFilter();
    typeFilter.setActive(Boolean.TRUE);
    productionTypeList = productionTypes.list(typeFilter).getItems();

    ProductionMetaFieldFilter metaFieldFilter = new ProductionMetaFieldFilter();
    metaFieldFilter.setFacet(true);
    List<ProductionMetaField> facetList = metaFields.list(metaFieldFilter).getItems();

    try {
      searchQuery = new SearchQuery();
      for (ProductionMetaField f : facetList) {
        SearchFacet sf = new SearchFacet();
        sf.setName(searchManager.toFacetName(f));
        sf.setLabel(f.getLabel());
        searchQuery.getFacets().add(sf);
      }
      searchQuery.setLimit(Integer.MAX_VALUE);

      searchResult = searchManager.search(searchQuery);
      searchQuery = searchResult.getSearchQuery();

    } catch (IOException ex) {
      Logger.getLogger(ProductionIndexView.class.getName()).log(
              Level.SEVERE, "Error getting search result.", ex);
    }
  }

  public void filter() {
    try {
      searchResult = searchManager.search(searchQuery);
      searchQuery = searchResult.getSearchQuery();
    } catch (IOException ex) {
      Logger.getLogger(ProductionIndexView.class.getName()).log(
              Level.SEVERE, "Error getting search result.", ex);
    }
  }

  public void gotoPage(int page) {
    searchResult.setCurrentPage(page);
    searchQuery = searchResult.getSearchQuery();
    filter();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the searchResult
   */
  public SearchResult<Production> getSearchResult() {
    return searchResult;
  }

  /**
   * @param searchResult the searchResult to set
   */
  public void setSearchResult(SearchResult<Production> searchResult) {
    this.searchResult = searchResult;
  }

  /**
   * @return the productionTypeList
   */
  public List<ProductionType> getProductionTypeList() {
    return productionTypeList;
  }

  /**
   * @param productionTypeList the productionTypeList to set
   */
  public void setProductionTypeList(List<ProductionType> productionTypeList) {
    this.productionTypeList = productionTypeList;
  }
  //</editor-fold>
}
