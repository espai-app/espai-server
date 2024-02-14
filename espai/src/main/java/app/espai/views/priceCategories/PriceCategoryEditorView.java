package app.espai.views.priceCategories;

import app.espai.dao.PriceCategories;
import app.espai.model.PriceCategory;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import org.primefaces.PrimeFaces;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class PriceCategoryEditorView implements Serializable {

  @EJB
  private PriceCategories priceCategories;

  private PriceCategory priceCategory;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String categoryIdParam = econtext.getRequestParameterMap().get("categoryId");
    if (categoryIdParam == null) {
      priceCategory = new PriceCategory();
      priceCategory.setDefaultValue(new Monetary(BigDecimal.ZERO, "EUR"));
    } else {
      priceCategory = priceCategories.get(Long.parseLong(categoryIdParam));
    }
  }

  public void save() {
    priceCategories.save(priceCategory);
    PrimeFaces.current().dialog().closeDynamic(true);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the priceCategory
   */
  public PriceCategory getPriceCategory() {
    return priceCategory;
  }

  /**
   * @param priceCategory the priceCategory to set
   */
  public void setPriceCategory(PriceCategory priceCategory) {
    this.priceCategory = priceCategory;
  }
  //</editor-fold>
}
