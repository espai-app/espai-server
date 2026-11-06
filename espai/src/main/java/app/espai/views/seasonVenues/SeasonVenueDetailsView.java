
package app.espai.views.seasonVenues;

import app.espai.RequestParameters;
import app.espai.dao.SeasonProductions;
import app.espai.dao.SeasonVenueProductions;
import app.espai.dao.SeasonVenues;
import app.espai.filter.SeasonProductionFilter;
import app.espai.filter.SeasonVenueProductionFilter;
import app.espai.model.Production;
import app.espai.model.SeasonProduction;
import app.espai.model.SeasonVenue;
import app.espai.model.SeasonVenueProduction;
import app.espai.views.BaseView;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class SeasonVenueDetailsView extends BaseView {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private SeasonVenues seasonVenues;

  @EJB
  private SeasonVenueProductions venueProductions;

  @EJB
  private SeasonProductions seasonProductions;

  private SeasonVenue seasonVenue;

  private List<SeasonVenueProduction> venueProductionList;
  private List<Production> allSeasonProductionList;
  private List<Production> selectedSeasonProductionList;

  @PostConstruct
  public void init() {

    Long seasonVenueId = RequestParameters.getParameterAsLong("seasonVenueId");
    if (seasonVenueId == null) {
      redirect(String.format("index.xhtml?seasonId=%d",
              RequestParameters.getParameterAsLong("seasonId")));
      return;
    }

    seasonVenue = seasonVenues.get(seasonVenueId);

    if (seasonVenue == null) {
      redirect(String.format("index.xhtml?seasonId=%d", seasonContext.getCurrentSeason().getId()));
      return;
    }

    SeasonVenueProductionFilter venueProductionFilter = new SeasonVenueProductionFilter();
    venueProductionFilter.setSeason(seasonContext.getCurrentSeason());
    venueProductionFilter.setVenue(seasonVenue.getVenue());

    venueProductionList = venueProductions.list(venueProductionFilter).getItems();

    SeasonProductionFilter productionFilter = new SeasonProductionFilter();
    productionFilter.setSeason(seasonContext.getCurrentSeason());
    allSeasonProductionList = seasonProductions
            .list(productionFilter)
            .getItems()
            .stream()
            .map(SeasonProduction::getProduction)
            .sorted((t1, t2) -> String.CASE_INSENSITIVE_ORDER.compare(t1.getTitle(), t2.getTitle()))
            .toList();

    selectedSeasonProductionList = new LinkedList<>(venueProductionList.stream()
            .map(SeasonVenueProduction::getProduction)
            .sorted((t1, t2) -> String.CASE_INSENSITIVE_ORDER.compare(t1.getTitle(), t2.getTitle()))
            .toList());
  }

  public void updateSelection() {

    // delete deselected productions
    List<SeasonVenueProduction> unselectedProductions = venueProductionList.stream()
            .filter(p -> !selectedSeasonProductionList.contains(p.getProduction()))
            .toList();

    unselectedProductions.forEach(p -> venueProductions.delete(p));

    // add missing productions
    List<Production> currentProductions = venueProductionList.stream()
            .map(SeasonVenueProduction::getProduction)
            .toList();
    List<Production> missingProductions = selectedSeasonProductionList.stream()
            .filter(p -> !currentProductions.contains(p))
            .toList();

    for (Production p : missingProductions) {
      SeasonVenueProduction vp = new SeasonVenueProduction();
      vp.setVenue(seasonVenue.getVenue());
      vp.setSeason(seasonContext.getCurrentSeason());
      vp.setProduction(p);

      venueProductions.save(vp);
    }

    // update list
    init();
  }

  public void addAllProductions() {

    List<Production> currentSelection = venueProductionList.stream()
            .map(SeasonVenueProduction::getProduction)
            .toList();

    List<Production> missingProductions = allSeasonProductionList.stream()
            .filter(p -> !currentSelection.contains(p))
            .toList();

    for (Production p : missingProductions) {
      SeasonVenueProduction vp = new SeasonVenueProduction();
      vp.setVenue(seasonVenue.getVenue());
      vp.setSeason(seasonContext.getCurrentSeason());
      vp.setProduction(p);

      venueProductions.save(vp);
    }

    init();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the seasonVenue
   */
  public SeasonVenue getSeasonVenue() {
    return seasonVenue;
  }

  /**
   * @param seasonVenue the seasonVenue to set
   */
  public void setSeasonVenue(SeasonVenue seasonVenue) {
    this.seasonVenue = seasonVenue;
  }

  /**
   * @return the venueProductionList
   */
  public List<SeasonVenueProduction> getVenueProductionList() {
    return venueProductionList;
  }

  /**
   * @param venueProductionList the venueProductionList to set
   */
  public void setVenueProductionList(List<SeasonVenueProduction> venueProductionList) {
    this.venueProductionList = venueProductionList;
  }

  /**
   * @return the allSeasonProductionList
   */
  public List<Production> getAllSeasonProductionList() {
    return allSeasonProductionList;
  }

  /**
   * @param allSeasonProductionList the allSeasonProductionList to set
   */
  public void setAllSeasonProductionList(List<Production> allSeasonProductionList) {
    this.allSeasonProductionList = allSeasonProductionList;
  }

  /**
   * @return the selectedSeasonProductionList
   */
  public List<Production> getSelectedSeasonProductionList() {
    return selectedSeasonProductionList;
  }

  /**
   * @param selectedSeasonProductionList the selectedSeasonProductionList to set
   */
  public void setSelectedSeasonProductionList(List<Production> selectedSeasonProductionList) {
    this.selectedSeasonProductionList = selectedSeasonProductionList;
  }
  //</editor-fold>

}
