package app.espai.views.seasons;

import app.espai.dao.Seasons;
import app.espai.filter.SeasonFilter;
import app.espai.model.Season;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class SeasonIndexView extends BaseView implements Serializable {

  @EJB
  private Seasons seasons;

  private Season newSeason = new Season();
  private List<Season> seasonList;

  @PostConstruct
  public void init() {
    SeasonFilter filter = new SeasonFilter();
    filter.setOrderBy("createDate");
    filter.setOrder(PageableFilter.Order.DESC);

    seasonList = seasons.list(filter).getItems();
  }

  public void create() {
    newSeason = seasons.save(newSeason);

    redirect("details.xhtml?seasonId=" + newSeason.getId(),
            "Spielzeit erstellt",
            null,
            FacesMessage.SEVERITY_INFO);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the seasons
   */
  public Seasons getSeasons() {
    return seasons;
  }

  /**
   * @param seasons the seasons to set
   */
  public void setSeasons(Seasons seasons) {
    this.seasons = seasons;
  }

  /**
   * @return the seasonList
   */
  public List<Season> getSeasonList() {
    return seasonList;
  }

  /**
   * @param seasonList the seasonList to set
   */
  public void setSeasonList(List<Season> seasonList) {
    this.seasonList = seasonList;
  }

  /**
   * @return the newSeason
   */
  public Season getNewSeason() {
    return newSeason;
  }

  /**
   * @param newSeason the newSeason to set
   */
  public void setNewSeason(Season newSeason) {
    this.newSeason = newSeason;
  }
  //</editor-fold>
}
