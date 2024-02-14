package app.espai.views.priceCategories;

import app.espai.dao.PriceCategories;
import app.espai.model.PriceCategory;
import app.espai.views.BaseView;
import app.espai.views.Dialog;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.PrimeFaces;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class PriceCategoryIndexView extends BaseView {

  @EJB
  private PriceCategories priceCategories;

  private List<? extends PriceCategory> priceCategoryList;

  @PostConstruct
  public void init() {
    priceCategoryList = priceCategories.list().getItems();
    priceCategoryList.sort(PriceCategories.DEFAULT_ORDER);
  }

  public void addCategory() {
    PrimeFaces.current().dialog().openDynamic("editor", Dialog.getDefaultOptions(400, 400), null);
  }

  public void editCategory(String id) {
    Map<String, List<String>> params = new HashMap<>();
    params.put("categoryId", Arrays.asList(id));
    PrimeFaces.current().dialog().openDynamic("editor", Dialog.getDefaultOptions(400, 400), params);
  }

  public void deleteCategory(Long id) {
    PriceCategory priceCategory = priceCategories.get(id);
    priceCategories.delete(priceCategory);

    redirect("Preiskategorie gelöscht", null);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the priceCategoryList
   */
  public List<? extends PriceCategory> getPriceCategoryList() {
    return priceCategoryList;
  }

  /**
   * @param priceCategoryList the priceCategoryList to set
   */
  public void setPriceCategoryList(List<? extends PriceCategory> priceCategoryList) {
    this.priceCategoryList = priceCategoryList;
  }
  //</editor-fold>

}
