package app.espai.views.venues;

import app.espai.dao.Venues;
import app.espai.filter.VenueFilter;
import app.espai.model.Venue;
import app.espai.views.BaseView;
import app.espai.views.Dialog;
import app.espai.views.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.util.List;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class VenueIndexView extends BaseView {

  @EJB
  private UserContext userContext;

  @EJB
  private Venues venues;

  private VenueFilter venueFilter;
  private List<Venue> venueList;

  @PostConstruct
  public void init() {
    if (userContext.isRestricted()) {
      venueList = userContext.getVenues();
    } else {
      ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

      venueFilter = new VenueFilter();
      venueFilter.setName(econtext.getRequestParameterMap().get("name"));
      venueFilter.setCity(econtext.getRequestParameterMap().get("city"));
      venueFilter.setOrderBy("city");
      venueFilter.setOrder(PageableFilter.Order.ASC);

      venueList = venues.list(venueFilter).getItems();
      venueList.sort(Venues.DEFAULT_ORDER);
    }
  }

  public void createVenue() {
    PrimeFaces.current().dialog().openDynamic(
            "editor.xhtml",
            Dialog.getDefaultOptions(600, 700),
            null);
  }

  public void onVenueCreated(SelectEvent<Long> event) {
    redirect("details.xhtml?venueId=" + event.getObject());
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the venueFilter
   */
  public VenueFilter getVenueFilter() {
    return venueFilter;
  }

  /**
   * @param venueFilter the venueFilter to set
   */
  public void setVenueFilter(VenueFilter venueFilter) {
    this.venueFilter = venueFilter;
  }

  /**
   * @return the venueList
   */
  public List<Venue> getVenueList() {
    return venueList;
  }

  /**
   * @param venueList the venueList to set
   */
  public void setVenueList(List<Venue> venueList) {
    this.venueList = venueList;
  }
  //</editor-fold>

}
