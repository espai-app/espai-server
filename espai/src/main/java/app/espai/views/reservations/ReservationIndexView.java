package app.espai.views.reservations;

import app.espai.businesslogic.ReservationManager;
import app.espai.dao.Productions;
import app.espai.dao.ReservationSummaries;
import app.espai.dao.Venues;
import app.espai.filter.ReservationSummaryFilter;
import app.espai.model.Production;
import app.espai.model.Reservation;
import app.espai.model.ReservationStatus;
import app.espai.model.ReservationSummary;
import app.espai.model.Venue;
import app.espai.views.SeasonContext;
import app.espai.views.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class ReservationIndexView implements Serializable {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private UserContext userContext;

  @EJB
  private ReservationSummaries reservationSummaries;

  @EJB
  private Venues venues;

  @EJB
  private Productions productions;

  @EJB
  private ReservationManager reservationManager;

  private List<ReservationSummary> reservationList;
  private List<Venue> venueList;
  private List<Production> productionList;
  private ReservationSummaryFilter reservationFilter;
  private List<ReservationSummary> selectedReservations;
  private Map<Long, List<Reservation>> childReservationMap = new HashMap<>();
  private boolean filtered;

  @PostConstruct
  public void init() {
    
    reservationFilter = new ReservationSummaryFilter();
    reservationFilter.setSeason(seasonContext.getCurrentSeason());

    if (userContext.isRestricted()) {
      reservationFilter.setVenues(userContext.getVenues());
    }
    List<ReservationSummary> allReservations = reservationSummaries.list(reservationFilter).getItems();

    reservationFilter = getFilter();
    reservationFilter.setSeason(seasonContext.getCurrentSeason());

    if (userContext.isRestricted()) {
      reservationFilter.setVenues(userContext.getVenues());
    }

    reservationFilter.setOrderBy("createDate");
    reservationFilter.setOrder(PageableFilter.Order.DESC);

    reservationList = reservationSummaries.list(reservationFilter).getItems();
    reservationList.sort(ReservationSummaries.DEFAULT_ORDER);

    venueList = allReservations.stream().map(ReservationSummary::getVenue).distinct().toList();
    productionList = allReservations.stream()
            .map(ReservationSummary::getProduction)
            .distinct()
            .sorted(((p1, p2) -> String.CASE_INSENSITIVE_ORDER.compare(p1.getTitle(), p2.getTitle())))
            .toList();
  }

  public void filter() {
    List<String> queryParams = new LinkedList<>();
    
    queryParams.add(String.format("seasonId=%d", seasonContext.getCurrentSeason().getId()));

    if (reservationFilter.getVenues() != null && !reservationFilter.getVenues().isEmpty()) {
      queryParams.addAll(reservationFilter.getVenues().stream()
                      .map(v -> String.format("venue=%d", v.getId())).toList());
    }

    if (reservationFilter.getProductions() != null && !reservationFilter.getProductions().isEmpty()) {
      queryParams.addAll(reservationFilter.getProductions().stream()
              .map(p -> String.format("production=%d", p.getId())).toList());
    }
    
    if (reservationFilter.getStatuses() != null && !reservationFilter.getStatuses().isEmpty()) {
      queryParams.addAll(reservationFilter.getStatuses().stream()
              .map(s -> String.format("status=%s", s.toString())).toList());
    }
    
    try {
      FacesContext context = FacesContext.getCurrentInstance();
      ExternalContext econtext = context.getExternalContext();
      econtext.redirect(String.format("index.xhtml?%s", String.join("&", queryParams)));
      context.responseComplete();
    } catch (IOException ex) {
      
    }
  }

  private ReservationSummaryFilter getFilter() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    ReservationSummaryFilter filter = new ReservationSummaryFilter();
    
    Map<String, String[]> params = econtext.getRequestParameterValuesMap();
    
    if (params.containsKey("venue")) {
      List<Venue> selectedVenues = new LinkedList<>();
      filter.setVenues(selectedVenues);
      for (String idParam : params.get("venue")) {
        long id = Long.parseLong(idParam);
        selectedVenues.add(venues.get(id));
      }
    }
    
    if (params.containsKey("production")) {
      List<Production> selectedProductions = new LinkedList<>();
      filter.setProductions(selectedProductions);
      for (String idParam : params.get("production")) {
        long id = Long.parseLong(idParam);
        selectedProductions.add(productions.get(id));
      }
    }
    
    if (params.containsKey("status")) {
      List<ReservationStatus> selectesStatuses = new LinkedList<>();
      filter.setStatuses(selectesStatuses);
      for (String idParam : params.get("status")) {
        selectesStatuses.add(ReservationStatus.valueOf(idParam));
      }
    }

    return filter;
  }

  public void confirmReservations() {
    reservationManager.confirmReservationSummaries(selectedReservations);
    init();
  }

  public void holdReservations() {
    reservationManager.holdReservationSummaries(selectedReservations);
    init();
  }

  public void cancelReservations() {
    reservationManager.cancelReservationSummaries(selectedReservations);
    init();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the seasonContext
   */
  public SeasonContext getSeasonContext() {
    return seasonContext;
  }

  /**
   * @param seasonContext the seasonContext to set
   */
  public void setSeasonContext(SeasonContext seasonContext) {
    this.seasonContext = seasonContext;
  }

  /**
   * @return the reservationList
   */
  public List<ReservationSummary> getReservationList() {
    return reservationList;
  }

  /**
   * @param reservationList the reservationList to set
   */
  public void setReservationList(List<ReservationSummary> reservationList) {
    this.reservationList = reservationList;
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

  /**
   * @return the productionList
   */
  public List<Production> getProductionList() {
    return productionList;
  }

  /**
   * @param productionList the productionList to set
   */
  public void setProductionList(List<Production> productionList) {
    this.productionList = productionList;
  }

  /**
   * @return the reservationFilter
   */
  public ReservationSummaryFilter getReservationFilter() {
    return reservationFilter;
  }

  /**
   * @param reservationFilter the reservationFilter to set
   */
  public void setReservationFilter(ReservationSummaryFilter reservationFilter) {
    this.reservationFilter = reservationFilter;
  }

  /**
   * @return the selectedReservations
   */
  public List<ReservationSummary> getSelectedReservations() {
    return selectedReservations;
  }

  /**
   * @param selectedReservations the selectedReservations to set
   */
  public void setSelectedReservations(List<ReservationSummary> selectedReservations) {
    this.selectedReservations = selectedReservations;
  }

  /**
   * @return the childReservationMap
   */
  public Map<Long, List<Reservation>> getChildReservationMap() {
    return childReservationMap;
  }

  /**
   * @param childReservationMap the childReservationMap to set
   */
  public void setChildReservationMap(Map<Long, List<Reservation>> childReservationMap) {
    this.childReservationMap = childReservationMap;
  }

  /**
   * @return the filtered
   */
  public boolean isFiltered() {
    return filtered;
  }

  /**
   * @param filtered the filtered to set
   */
  public void setFiltered(boolean filtered) {
    this.filtered = filtered;
  }
//</editor-fold>
}
