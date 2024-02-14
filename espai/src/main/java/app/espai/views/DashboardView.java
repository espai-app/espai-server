package app.espai.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class DashboardView extends BaseView implements Serializable {

  private static final long serialVersionUID = 1L;

    private DefaultDashboardModel model;
    private String principle;

    @PostConstruct
    public void init() {
        // responsive
        model = new DefaultDashboardModel();
        DefaultDashboardColumn firstColumn = new DefaultDashboardColumn();
        firstColumn.addWidget("welcome");
        model.addColumn(firstColumn);

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        if (ec.getUserPrincipal() != null) {
            principle = ec.getUserPrincipal().getName();
        }
    }

    /**
     * @return the model
     */
    public DefaultDashboardModel getModel() {
        return model;
    }

    /**
     * @return the principle
     */
    public String getPrinciple() {
        return principle;
    }

    /**
     * @param principle the principle to set
     */
    public void setPrinciple(String principle) {
        this.principle = principle;
    }

}
