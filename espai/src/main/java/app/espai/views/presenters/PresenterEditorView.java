package app.espai.views.presenters;

import app.espai.dao.Presenters;
import app.espai.model.Presenter;
import app.espai.views.BaseView;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class PresenterEditorView extends BaseView {

  @EJB
  private Presenters presenters;

  private Presenter presenter;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String presenterIdParam = econtext.getRequestParameterMap().get("presenterId");
    if (presenterIdParam != null && presenterIdParam.matches("\\d+")) {
      presenter = presenters.get(Long.parseLong(presenterIdParam));
    } else {
      presenter = new Presenter();
    }
  }

  public void save() {
    presenters.save(presenter);
    redirect("index.xhtml", "Referent:in gespeichert.", null, FacesMessage.SEVERITY_INFO);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the presenter
   */
  public Presenter getPresenter() {
    return presenter;
  }

  /**
   * @param presenter the presenter to set
   */
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }
  //</editor-fold>
}
