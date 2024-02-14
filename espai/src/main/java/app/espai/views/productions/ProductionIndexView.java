package app.espai.views.productions;

import app.espai.dao.Productions;
import app.espai.filter.ProductionFilter;
import app.espai.model.Production;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@Stateful
@RequestScoped
@RolesAllowed({"MANAGER"})
public class ProductionIndexView {

  @EJB
  private Productions productions;

  private List<Production> productionList;

  @PostConstruct
  public void init() {

    ProductionFilter productionFilter = new ProductionFilter();
    productionFilter.setOrderBy("title");
    productionFilter.setOrder(PageableFilter.Order.ASC);
    productionList = productions.list(productionFilter).getItems();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the productionList
   */
  public List<Production> getProductionList() {
    return productionList;
  }

  /**
   * @param productionList the productionList to set
   */
  public void setProductionList(List<Production> productionList) {
    this.productionList = productionList;
  }
  //</editor-fold>
}
