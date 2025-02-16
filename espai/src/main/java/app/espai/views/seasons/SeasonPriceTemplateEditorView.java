/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.seasons;

import app.espai.dao.PriceCategories;
import app.espai.dao.SeasonPriceTemplates;
import app.espai.dao.Seasons;
import app.espai.model.PriceCategory;
import app.espai.model.SeasonPriceTemplate;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.math.BigDecimal;
import java.util.List;
import org.primefaces.PrimeFaces;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class SeasonPriceTemplateEditorView {
  
  @EJB
  private SeasonPriceTemplates priceTemplates;
  
  @EJB
  private PriceCategories priceCategories;
  
  @EJB
  private Seasons seasons;
  
  private SeasonPriceTemplate priceTemplate;
  private List<? extends PriceCategory> priceCategoryList;
  
  @PostConstruct
  public void init() {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext econtext = context.getExternalContext();
    
    String priceTemplateIdParam = econtext.getRequestParameterMap().get("priceTemplateId");
    String seasonIdParam = econtext.getRequestParameterMap().get("seasonId");
    
    if (priceTemplateIdParam != null && priceTemplateIdParam.matches("\\d+")) {
      priceTemplate = priceTemplates.get(Long.parseLong(priceTemplateIdParam));
    } else if (seasonIdParam != null && seasonIdParam.matches("\\d+")) {
      priceTemplate = new SeasonPriceTemplate();
      priceTemplate.setSeason(seasons.get(Long.parseLong(seasonIdParam)));
      priceTemplate.setPrice(new Monetary(BigDecimal.ZERO, "EUR"));
    } else {
      throw new RuntimeException("Ids missing.");
    }
    
    priceCategoryList = priceCategories.list().getItems();
  }
  
  public void save() {
    priceTemplates.save(priceTemplate);
    PrimeFaces.current().dialog().closeDynamic(priceTemplate);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the priceTemplate
   */
  public SeasonPriceTemplate getPriceTemplate() {
    return priceTemplate;
  }
  
  /**
   * @param priceTemplate the priceTemplate to set
   */
  public void setPriceTemplate(SeasonPriceTemplate priceTemplate) {
    this.priceTemplate = priceTemplate;
  }

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
