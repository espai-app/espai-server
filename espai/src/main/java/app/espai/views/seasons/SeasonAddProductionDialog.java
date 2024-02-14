package app.espai.views.seasons;

import app.espai.dao.Productions;
import app.espai.dao.SeasonProductions;
import app.espai.dao.Seasons;
import app.espai.filter.SeasonProductionFilter;
import app.espai.model.Production;
import app.espai.model.Season;
import app.espai.model.SeasonProduction;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class SeasonAddProductionDialog implements Serializable {

  @EJB
  private Seasons seasons;

  @EJB
  private Productions productions;

  @EJB
  private SeasonProductions seasonProductions;

  private Season season;
  private List<? extends Production> productionList;
  private List<Production> selectedProductions;

  @PostConstruct
  public void init() {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    season = seasons.get(Long.parseLong(externalContext.getRequestParameterMap().get("seasonId")));

    SeasonProductionFilter spFilter = new SeasonProductionFilter();
    spFilter.setSeason(season);
    List<SeasonProduction> currentProductions = seasonProductions.list(spFilter).getItems();
    List<Long> currentIds = currentProductions
            .stream()
            .map(p -> p.getProduction().getId())
            .toList();

    productionList = productions.list()
            .getItems()
            .stream()
            .filter(i -> {return !currentIds.contains(i.getId());})
            .sorted(((p1, p2) -> String.CASE_INSENSITIVE_ORDER.compare(p1.getTitle(), p2.getTitle())))
            .toList();
  }

  public void addSelected() {
    for (Production p : selectedProductions) {
      SeasonProduction s = new SeasonProduction();
      s.setSeason(season);
      s.setProduction(p);

      seasonProductions.save(s);
    }

     PrimeFaces.current().dialog().closeDynamic(null);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the season
   */
  public Season getSeason() {
    return season;
  }

  /**
   * @param season the season to set
   */
  public void setSeason(Season season) {
    this.season = season;
  }

  /**
   * @return the productionList
   */
  public List<? extends Production> getProductionList() {
    return productionList;
  }

  /**
   * @param productionList the productionList to set
   */
  public void setProductionList(List<Production> productionList) {
    this.productionList = productionList;
  }

  /**
   * @return the selectedProductions
   */
  public List<Production> getSelectedProductions() {
    return selectedProductions;
  }

  /**
   * @param selectedProductions the selectedProductions to set
   */
  public void setSelectedProductions(List<Production> selectedProductions) {
    this.selectedProductions = selectedProductions;
  }
  //</editor-fold>
}
