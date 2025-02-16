package app.espai.views.seasons;

import app.espai.Constants;
import app.espai.dao.SeasonProductions;
import app.espai.dao.SeasonVenues;
import app.espai.dao.Seasons;
import app.espai.filter.SeasonProductionFilter;
import app.espai.model.Season;
import app.espai.model.SeasonProduction;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DialogFrameworkOptions;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class SeasonDetailsView extends BaseView implements Serializable {

  @EJB
  private Seasons seasons;

  @EJB
  private SeasonProductions seasonProductions;

  @EJB
  private SeasonVenues seasonVenues;

  private Season season;
  private List<SeasonProduction> productionList;
  private List<SeasonProduction> selectedProductions;  

  @PostConstruct
  public void init() {

    try {

      FacesContext context = FacesContext.getCurrentInstance();
      ExternalContext externalContext = context.getExternalContext();

      String seasonId = externalContext.getRequestParameterMap().get("seasonId");

      if (seasonId != null && seasonId.matches("\\d+")) {
        season = seasons.get(Long.parseLong(seasonId));
        loadProductions();
        return;
      }

      externalContext.dispatch("/WEB-INF/errors/notFound.xhtml");
    } catch (IOException | NumberFormatException | ResourceNotFoundException ex) {
      Logger.getLogger(SeasonDetailsView.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void toggleArchive() {
    season.setArchived(!season.isArchived());
    seasons.save(season);
    redirect(
            season.isArchived() ? "Spielzeit archiviert." : "Spielzeit wiederhergestellt.",
            null);
  }

  public void saveSeason() {
    seasons.save(season);
    redirect("details.xhtml?" + Constants.SEASON_ID + "=" + season.getId(),
            "Spielzeit wurde gespeichert.",
            null,
            FacesMessage.SEVERITY_INFO);
  }

  public void deleteSeason() {
    seasons.delete(season);
    redirect("index.xhtml", "Spielzeit wurde gelöscht.", null, FacesMessage.SEVERITY_INFO);
  }

  public void addProductions() {
    DialogFrameworkOptions options = DialogFrameworkOptions.builder()
            .headerElement("Programmpunkte hinzufügen")
            .width("900px")
            .height("630px")
            .contentWidth("100%")
            .modal(true)
            .responsive(true)
            .resizable(true)
            .build();

    Map<String, List<String>> params = new HashMap<>();
    params.put("seasonId", Arrays.asList(String.valueOf(season.getId())));

    PrimeFaces.current().dialog().openDynamic("addProductions", options, params);
  }

  public void loadProductions() {
    SeasonProductionFilter productionFilter = new SeasonProductionFilter();
    productionFilter.setSeason(season);
    productionList = seasonProductions.list(productionFilter).getItems();
    productionList.sort((p1, p2) -> {return String.CASE_INSENSITIVE_ORDER.compare(p1.getProduction().getTitle(), p2.getProduction().getTitle());});
    PrimeFaces.current().ajax().update("productionList");
  }

  public void deleteProductions() {
    for (ListIterator<SeasonProduction> i = selectedProductions.listIterator(); i.hasNext();) {
      seasonProductions.delete(i.next());
      i.remove();
    }
    loadProductions();
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
  public List<SeasonProduction> getProductionList() {
    return productionList;
  }

  /**
   * @param productionList the productionList to set
   */
  public void setProductionList(List<SeasonProduction> productionList) {
    this.productionList = productionList;
  }

  /**
   * @return the selectedProductions
   */
  public List<SeasonProduction> getSelectedProductions() {
    return selectedProductions;
  }

  /**
   * @param selectedProductions the selectedProductions to set
   */
  public void setSelectedProductions(List<SeasonProduction> selectedProductions) {
    this.selectedProductions = selectedProductions;
  }
  //</editor-fold>

}
