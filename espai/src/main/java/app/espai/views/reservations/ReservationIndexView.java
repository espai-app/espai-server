package app.espai.views.reservations;

import app.espai.Mailer;
import app.espai.ReservationMailManager;
import app.espai.dao.Productions;
import app.espai.dao.ReservationSummaries;
import app.espai.dao.Reservations;
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
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rocks.xprs.mail.Mail;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class ReservationIndexView {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private UserContext userContext;

  @EJB
  private ReservationSummaries reservationSummaries;

  @EJB
  private Reservations reservations;

  @EJB
  private Venues venues;

  @EJB
  private Productions productions;

  @EJB
  private Mailer mailer;

  @EJB
  private ReservationMailManager reservationMailManager;

  private List<ReservationSummary> reservationList;
  private List<Venue> venueList;
  private List<Production> productionList;
  private ReservationSummaryFilter reservationFilter;
  private List<ReservationSummary> selectedReservations;
  private List<Venue> selectedVenues;
  private List<Long> selectedVenueIds;
  private List<Production> selectedProductions;
  private List<ReservationStatus> selectedStatuses;
  private List<Long> selectedProductionIds;
  private Map<Long, List<Reservation>> childReservationMap = new HashMap<>();
  private boolean filtered;

  @PostConstruct
  public void init() {

    reservationFilter = getFilter();
    reservationFilter.setSeason(seasonContext.getCurrentSeason());

    if (userContext.isRestricted()) {
      reservationFilter.setVenues(userContext.getVenues());
    }

    reservationFilter.setOrderBy("createDate");
    reservationFilter.setOrder(PageableFilter.Order.DESC);

    reservationList = reservationSummaries.list(reservationFilter).getItems();
    reservationList.sort(ReservationSummaries.DEFAULT_ORDER);

    venueList = reservationList.stream().map(ReservationSummary::getVenue).distinct().toList();
    productionList = reservationList.stream().map(ReservationSummary::getProduction).distinct().toList();
  }

  private ReservationSummaryFilter getFilter() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    ReservationSummaryFilter filter = new ReservationSummaryFilter();

    for (Map.Entry<String, String[]> entry : econtext.getRequestParameterValuesMap().entrySet()) {
      if (entry.getKey().endsWith(":venue")) {

        selectedVenues = new LinkedList<Venue>();
        for (String idParam : entry.getValue()) {
          long id = Long.parseLong(idParam);
          selectedVenues.add(venues.get(id));
          selectedVenueIds.add(id);
        }

        if (!selectedVenues.isEmpty()) {
          filter.setVenues(selectedVenues);
          filtered |= true;
        }
      }

      if (entry.getKey().endsWith(":production")) {
        selectedProductions = new LinkedList<>();
        for (String idParam : entry.getValue()) {
          long id = Long.parseLong(idParam);
          selectedProductions.add(productions.get(id));
          selectedProductionIds.add(id);
        }

        if (!selectedProductions.isEmpty()) {
          filter.setProductions(selectedProductions);
          filtered |= true;
        }
      }

      if (entry.getKey().endsWith(":status")) {
        selectedStatuses = new LinkedList<>();
        for (String statusName : entry.getValue()) {
          selectedStatuses.add(ReservationStatus.valueOf(statusName));
        }

        if (!selectedStatuses.isEmpty()) {
          filtered |= true;
        }
      }
    }

    return filter;
  }

  public void confirmReservations() {
    int counter = 0;
    for (ReservationSummary s : selectedReservations) {
      Reservation r = reservations.get(s.getId());
      r.setStatus(ReservationStatus.CONFIRMED);
      reservations.save(r);

      try {
        Mail mail = reservationMailManager.createConfirmed(r);
        Message message = mailer.send(mail);
        mailer.saveSent(message);
        counter++;
      } catch (MessagingException | IOException ex) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "E-Mail für Buchungen %d nicht gesendet.".formatted(s.getId()), null));
        Logger.getLogger(ReservationIndexView.class.getName()).log(
                Level.WARNING,
                "Failed to send confirmation message for reservation no. %d".formatted(r.getId()),
                ex);
      }
    }

    init();
    FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("%d Buchungen bestätigt.".formatted(counter)));
  }

  public void holdReservations() {
    int counter = 0;
    for (ReservationSummary s : selectedReservations) {
      Reservation r = reservations.get(s.getId());
      r.setStatus(ReservationStatus.HOLD);
      reservations.save(r);

      try {
        Mail mail = reservationMailManager.createHold(r);
        Message message = mailer.send(mail);
        mailer.saveSent(message);
        counter++;
      } catch (MessagingException | IOException ex) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "E-Mail für Buchungen %d nicht gesendet.".formatted(s.getId()), null));
        Logger.getLogger(ReservationIndexView.class.getName()).log(
                Level.WARNING,
                "Failed to send confirmation message for reservation no. %d".formatted(r.getId()),
                ex);
      }
    }
    init();
    FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("%d Buchungen geparkt.".formatted(counter)));
  }

  public void cancelReservations() {
    int counter = 0;
    for (ReservationSummary s : selectedReservations) {
      Reservation r = reservations.get(s.getId());
      r.setStatus(ReservationStatus.CANCELED);
      reservations.save(r);

      try {
        Mail mail = reservationMailManager.createCanceled(r);
        Message message = mailer.send(mail);
        mailer.saveSent(message);
        counter++;
      } catch (MessagingException | IOException ex) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "E-Mail für Buchungen %d nicht gesendet.".formatted(s.getId()), null));
        Logger.getLogger(ReservationIndexView.class.getName()).log(
                Level.WARNING,
                "Failed to send confirmation message for reservation no. %d".formatted(r.getId()),
                ex);
      }
    }
    init();
    FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Buchungen storniert.".formatted(counter)));
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
   * @return the selectedVenues
   */
  public List<Venue> getSelectedVenues() {
    return selectedVenues;
  }

  /**
   * @param selectedVenues the selectedVenues to set
   */
  public void setSelectedVenues(List<Venue> selectedVenues) {
    this.selectedVenues = selectedVenues;
  }

  /**
   * @return the selectedVenueIds
   */
  public List<Long> getSelectedVenueIds() {
    return selectedVenueIds;
  }

  /**
   * @param selectedVenueIds the selectedVenueIds to set
   */
  public void setSelectedVenueIds(List<Long> selectedVenueIds) {
    this.selectedVenueIds = selectedVenueIds;
  }

  /**
   * @return the selectedProductions
   */
  public List<Production> getSelectedProductions() {
    return selectedProductions;
  }

  /**
   * @param selectedProductions the selectedProductions to set
   */
  public void setSelectedProductions(List<Production> selectedProductions) {
    this.selectedProductions = selectedProductions;
  }

  /**
   * @return the selectedStatuses
   */
  public List<ReservationStatus> getSelectedStatuses() {
    return selectedStatuses;
  }

  /**
   * @param selectedStatuses the selectedStatuses to set
   */
  public void setSelectedStatuses(List<ReservationStatus> selectedStatuses) {
    this.selectedStatuses = selectedStatuses;
  }

  /**
   * @return the selectedProductionIds
   */
  public List<Long> getSelectedProductionIds() {
    return selectedProductionIds;
  }

  /**
   * @param selectedProductionIds the selectedProductionIds to set
   */
  public void setSelectedProductionIds(List<Long> selectedProductionIds) {
    this.selectedProductionIds = selectedProductionIds;
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
