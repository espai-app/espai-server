package app.espai.views.presenters;

import app.espai.dao.Presenters;
import app.espai.model.Presenter;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class PresenterIndexView {

  @EJB
  private Presenters presenters;

  private List<? extends Presenter> presenterList;

  @PostConstruct
  public void init() {
    presenterList = presenters.list().getItems();
    presenterList.sort(Presenters.DEFAULT_ORDER);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the presenterList
   */
  public List<? extends Presenter> getPresenterList() {
    return presenterList;
  }

  /**
   * @param presenterList the presenterList to set
   */
  public void setPresenterList(List<? extends Presenter> presenterList) {
    this.presenterList = presenterList;
  }
  //</editor-fold>
}
