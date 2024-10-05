/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.analytics;

import app.espai.dao.ReservationSummaries;
import app.espai.filter.ReservationSummaryFilter;
import app.espai.model.ReservationSummary;
import app.espai.views.SeasonContext;
import app.espai.views.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import rocks.xprs.runtime.exceptions.AccessDeniedException;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class AnalyticsIndexView {

  @EJB
  private UserContext userContext;

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private ReservationSummaries reservations;

  private List<CityStatistics> cityStatistics;
  private CityStatistics cityTotals;

  @PostConstruct
  public void init() {

    if (userContext.isRestricted()) {
      throw new AccessDeniedException();
    }

    ReservationSummaryFilter reservationFilter = new ReservationSummaryFilter();
    reservationFilter.setSeason(seasonContext.getCurrentSeason());

    List<ReservationSummary> reservationList = reservations
            .list(reservationFilter).getItems();

    HashMap<String, CityStatistics> ticketsPerCityMap = new HashMap<>();
    cityTotals = new CityStatistics("Gesamt");

    for (ReservationSummary r : reservationList) {
      if (r.getParentReservation() != null) {
        continue;
      }

      String currentCity = r.getVenue().getCity();
      if (!ticketsPerCityMap.containsKey(currentCity)) {
        ticketsPerCityMap.put(currentCity, new CityStatistics(currentCity));
      }

      ticketsPerCityMap.get(currentCity).add(
              r.getStatus().toString(), r.getTickets());
      ticketsPerCityMap.get(currentCity).add("totals", r.getTickets());

      cityTotals.add(r.getStatus().toString(), r.getTickets());
      cityTotals.add("totals", r.getTickets());
    }

    cityStatistics = new LinkedList<>(ticketsPerCityMap.values());
    cityStatistics.sort((c1, c2) -> String.CASE_INSENSITIVE_ORDER
            .compare(c1.getName(), c2.getName()));
    cityStatistics.add(cityTotals);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the cityStatistics
   */
  public List<CityStatistics> getCityStatistics() {
    return cityStatistics;
  }

  /**
   * @param cityStatistics the cityStatistics to set
   */
  public void setCityStatistics(List<CityStatistics> cityStatistics) {
    this.cityStatistics = cityStatistics;
  }

  /**
   * @return the cityTotals
   */
  public CityStatistics getCityTotals() {
    return cityTotals;
  }

  /**
   * @param cityTotals the cityTotals to set
   */
  public void setCityTotals(CityStatistics cityTotals) {
    this.cityTotals = cityTotals;
  }
  //</editor-fold>

  public static class CityStatistics {

    private String name;
    private HashMap<String, TicketStatistics> statistics;

    public CityStatistics() {

    }

    public CityStatistics(String name) {
      this.name = name;
    }

    public void add(String statusString, int tickets) {
      if (statistics == null) {
        statistics = new HashMap<>();
      }

      if (!statistics.containsKey(statusString)) {
        statistics.put(statusString, new TicketStatistics(0, 0));
      }

      statistics.get(statusString).add(tickets);
    }

    public TicketStatistics get(String status) {
      if (!statistics.containsKey(status)) {
        return new TicketStatistics(0, 0);
      }
      return statistics.get(status);
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    /**
     * @return the name
     */
    public String getName() {
      return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
      this.name = name;
    }

    /**
     * @return the statistics
     */
    public HashMap<String, TicketStatistics> getStatistics() {
      return statistics;
    }

    /**
     * @param statistics the statistics to set
     */
    public void setStatistics(HashMap<String, TicketStatistics> statistics) {
      this.statistics = statistics;
    }
    //</editor-fold>
  }

  public static class TicketStatistics {

    private int reservations;
    private int tickets;

    public TicketStatistics() {

    }

    public TicketStatistics(int reservations, int tickets) {
      this.reservations = reservations;
      this.tickets = tickets;
    }

    public void add(int tickets) {
      this.reservations++;
      this.tickets += tickets;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    /**
     * @return the reservations
     */
    public int getReservations() {
      return reservations;
    }

    /**
     * @param reservations the reservations to set
     */
    public void setReservations(int reservations) {
      this.reservations = reservations;
    }

    /**
     * @return the tickets
     */
    public int getTickets() {
      return tickets;
    }

    /**
     * @param tickets the tickets to set
     */
    public void setTickets(int tickets) {
      this.tickets = tickets;
    }
    //</editor-fold>
  }

}
