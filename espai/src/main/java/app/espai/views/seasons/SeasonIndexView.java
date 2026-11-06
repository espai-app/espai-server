package app.espai.views.seasons;

import app.espai.businesslogic.SeasonManager;
import app.espai.dao.ReservationForms;
import app.espai.dao.Seasons;
import app.espai.filter.ReservationFormFilter;
import app.espai.filter.SeasonFilter;
import app.espai.model.ReservationForm;
import app.espai.model.Season;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
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
  
  @EJB
  private ReservationForms reservationForms;
  
  @Inject
  private SeasonManager seasonManager;

  private Season newSeason = new Season();
  private Season seasonTemplate;
  private List<Season> seasonList;
  private List<ReservationForm> reservationFormList;

  @PostConstruct
  public void init() {
    SeasonFilter filter = new SeasonFilter();
    filter.setOrderBy("createDate");
    filter.setOrder(PageableFilter.Order.DESC);
    
    seasonList = seasons.list(filter).getItems();
    
    ReservationFormFilter reservationFormFilter = new ReservationFormFilter();
    reservationFormList = reservationForms.list(reservationFormFilter).getItems();
  }

  public void create() {
    
    if (seasonTemplate != null) {
      newSeason = seasonManager.createFromTemplate(newSeason, seasonTemplate);
    } else {
      newSeason = seasons.save(newSeason);
    }

    redirect("../../seasons/settings/index.xhtml?seasonId=" + newSeason.getId(),
            "Spielzeit erstellt",
            null,
            FacesMessage.SEVERITY_INFO);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
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

  /**
   * @return the seasonTemplate
   */
  public Season getSeasonTemplate() {
    return seasonTemplate;
  }

  /**
   * @param seasonTemplate the seasonTemplate to set
   */
  public void setSeasonTemplate(Season seasonTemplate) {
    this.seasonTemplate = seasonTemplate;
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
   * @return the reservationFormList
   */
  public List<ReservationForm> getReservationFormList() {
    return reservationFormList;
  }

  /**
   * @param reservationFormList the reservationFormList to set
   */
  public void setReservationFormList(List<ReservationForm> reservationFormList) {
    this.reservationFormList = reservationFormList;
  }
  //</editor-fold>
}
