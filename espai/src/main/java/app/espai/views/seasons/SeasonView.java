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
    selectedSeason = seasonContext.getCurrentSeason();

    if (selectedSeason == null && !activeSeasons.isEmpty()) {
      try {
        ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

        HttpServletRequest request = (HttpServletRequest) econtext.getRequest();

        StringBuffer url = request.getRequestURL();

        econtext.redirect(url.append("?").append(Constants.SEASON_ID).append("=").append(activeSeasons.get(0).getId()).toString());
      } catch (IOException ex) {
        // should not happen
        throw new RuntimeException(ex);
      }
    }
  }

  public void onSeasonChanged(ValueChangeEvent event) {
    try {
      ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
      HttpServletRequest request = (HttpServletRequest) econtext.getRequest();
      StringBuffer url = request.getRequestURL();

      econtext.redirect(url.append("?").append(Constants.SEASON_ID).append("=")
              .append(((Season) event.getNewValue()).getId()).toString());
    } catch (IOException ex) {
      // should not happen
      throw new RuntimeException(ex);
    }
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
