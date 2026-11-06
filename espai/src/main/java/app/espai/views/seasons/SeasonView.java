package app.espai.views.seasons;

import app.espai.Constants;
import app.espai.dao.Seasons;
import app.espai.filter.SeasonFilter;
import app.espai.model.Season;
import app.espai.views.BaseView;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class SeasonView extends BaseView {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private Seasons seasons;

  private Season selectedSeason;
  private List<Season> activeSeasons;

  @PostConstruct
  public void init() {
    SeasonFilter filter = new SeasonFilter();
    filter.setArchived(Boolean.FALSE);
    filter.setOrder(PageableFilter.Order.DESC);
    filter.setOrderBy("start");

    activeSeasons = seasons.list(filter).getItems();

    if (!activeSeasons.contains(seasonContext.getCurrentSeason())) {
      activeSeasons = new LinkedList<>(activeSeasons);
      activeSeasons.add(seasonContext.getCurrentSeason());
    }
    selectedSeason = seasonContext.getCurrentSeason();

    if (selectedSeason == null && !activeSeasons.isEmpty()) {
      try {
        ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
        econtext.redirect(getSeasonIndex(activeSeasons.get(0)));
      } catch (IOException ex) {
        // should not happen
        throw new RuntimeException(ex);
      }
    }
  }

  public void onSeasonChanged(ValueChangeEvent event) {
    try {
      ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
      econtext.redirect(getSeasonIndex(((Season) event.getNewValue())));

    } catch (IOException ex) {
      // should not happen
      throw new RuntimeException(ex);
    }
  }

  private String getSeasonIndex(Season season) {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    HttpServletRequest request = (HttpServletRequest) econtext.getRequest();

    StringBuffer url = request.getRequestURL();
    int pathIndex = url.indexOf("/", 8);
    url.setLength(pathIndex);
    url.append(request.getContextPath())
            .append("/seasons/events/index.xhtml?")
            .append(Constants.SEASON_ID).append("=").append(season.getId());

    return url.toString();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the selectedSeason
   */
  public Season getSelectedSeason() {
    return selectedSeason;
  }

  /**
   * @param selectedSeason the selectedSeason to set
   */
  public void setSelectedSeason(Season selectedSeason) {
    this.selectedSeason = selectedSeason;
  }

  /**
   * @return the activeSeasons
   */
  public List<Season> getActiveSeasons() {
    return activeSeasons;
  }

  /**
   * @param activeSeasons the activeSeasons to set
   */
  public void setActiveSeasons(List<Season> activeSeasons) {
    this.activeSeasons = activeSeasons;
  }
  //</editor-fold>
}
