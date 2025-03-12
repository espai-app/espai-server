/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.webservice;

import app.espai.dao.Events;
import app.espai.dao.ReservationExtras;
import app.espai.dao.ReservationTickets;
import app.espai.dao.Reservations;
import app.espai.dao.Seasons;
import app.espai.filter.EventFilter;
import app.espai.filter.ReservationExtraFilter;
import app.espai.filter.ReservationFilter;
import app.espai.filter.ReservationTicketFilter;
import app.espai.model.Event;
import app.espai.model.PriceCategory;
import app.espai.model.Reservation;
import app.espai.model.ReservationExtra;
import app.espai.model.ReservationTicket;
import app.espai.model.Season;
import app.espai.model.SeatCategory;
import app.espai.views.UserContext;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author rborowski
 */
@Stateless
@Path("export")
public class ExportWebservice {

  @EJB
  private UserContext userContext;

  @EJB
  private Seasons seasons;

  @EJB
  private Events events;

  @EJB
  private Reservations reservations;

  @EJB
  private ReservationTickets tickets;

  @EJB
  private ReservationExtras extras;

  @GET
  @Path("season/{seasonId}/reservations")
  public Response exportReservations(@PathParam("seasonId") Long seasonId) {

    if (userContext.getCurrentPrincipal() == null) {
      return Response
              .status(Response.Status.UNAUTHORIZED)
              .build();
    }

    // collect events, reservations, tickets...
    Season season = seasons.get(seasonId);
    EventFilter eventFilter = userContext.getEventFilter();
    eventFilter.setSeason(season);
    List<Event> eventList = events.list(eventFilter).getItems();

    ReservationFilter reservationFilter = new ReservationFilter();
    reservationFilter.setEvents(eventList);
    List<Reservation> reservationList = reservations.list(reservationFilter).getItems();

    ReservationTicketFilter ticketFilter = new ReservationTicketFilter();
    ticketFilter.setReservations(reservationList);
    List<ReservationTicket> ticketList = tickets.list(ticketFilter).getItems();

    ReservationExtraFilter extraFilter = new ReservationExtraFilter();
    extraFilter.setReservations(reservationList);
    List<ReservationExtra> extraList = extras.list(extraFilter).getItems();

    // group data by reservation
    Map<Long, List<ReservationTicket>> ticketMap = ticketList.stream().collect(
            Collectors.groupingBy(r -> r.getReservation().getId()));

    List<PriceCategory> priceCategoryList = ticketList.stream()
            .map(ReservationTicket::getPriceCategory)
            .distinct()
            .toList();

    List<SeatCategory> seatCategoryList = ticketList.stream()
            .map(ReservationTicket::getSeatCategory)
            .distinct()
            .toList();

    List<String> combinedCategoryList = new LinkedList<>();
    for (SeatCategory sc : seatCategoryList) {
      for (PriceCategory pc : priceCategoryList) {
        combinedCategoryList.add(String.format("%s - %s", sc.getName(), pc.getName()));
      }
    }

    Map<Long, List<ReservationExtra>> extraMap = extraList.stream()
            .collect(Collectors.groupingBy(r -> r.getReservation().getId()));

    List<String> extraCategories = extraList.stream()
            .map(ReservationExtra::getFieldName)
            .distinct()
            .toList();

    // generate lines for the export
    List<ReservationExport> reservationExports = new LinkedList<>();
    for (Reservation r : reservationList) {

      Map<String, Integer> ticketsByCategory = new HashMap<>();
      combinedCategoryList.forEach(c -> ticketsByCategory.put(c, 0));

      for (ReservationTicket t : ticketMap.getOrDefault(r.getId(), Collections.emptyList())) {
        String categoryName = String.format("%s - %s",
                t.getSeatCategory().getName(), t.getPriceCategory().getName());

        ticketsByCategory.put(categoryName, ticketsByCategory.get(categoryName) + t.getAmount());
      }

      Map<String, String> extrasForReservation = new HashMap<>();
      extraCategories.forEach(e -> extrasForReservation.put(e, ""));
      for (ReservationExtra e : extraMap.getOrDefault(r.getId(), Collections.emptyList())) {
        extrasForReservation.put(e.getFieldName(), e.getValue());
      }

      reservationExports.add(new ReservationExport(r, ticketsByCategory, extrasForReservation));
    }

    return Response
            .ok(
                    new CsvResponse(
                            reservationExports,
                            combinedCategoryList,
                            extraCategories,
                            userContext.isRestricted()),
                    "application/csv")
            .header("Content-Disposition",
                    "attachment; filename=\"Reservierungen " + season.getName() + ".csv\"")
            .build();
  }

  private static class ReservationExport {

    private Reservation reservation;
    private Map<String, Integer> tickets;
    private Map<String, String> extras;

    public ReservationExport(Reservation reservation, Map<String, Integer> ticketsByCategory,
            Map<String, String> reservationMeta) {
      this.reservation = reservation;
      this.tickets = ticketsByCategory;
      this.extras = reservationMeta;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    /**
     * @return the reservation
     */
    public Reservation getReservation() {
      return reservation;
    }

    /**
     * @param reservation the reservation to set
     */
    public void setReservation(Reservation reservation) {
      this.reservation = reservation;
    }

    /**
     * @return the ticketsByCategory
     */
    public Map<String, Integer> getTickets() {
      return tickets;
    }

    /**
     * @param tickets the tickets to set
     */
    public void setTickets(Map<String, Integer> tickets) {
      this.tickets = tickets;
    }

    /**
     * @return the reservationMeta
     */
    public Map<String, String> getExtras() {
      return extras;
    }

    /**
     * @param extras the extras to set
     */
    public void setExtras(Map<String, String> extras) {
      this.extras = extras;
    }
    //</editor-fold>

  }

  private static class CsvResponse implements StreamingOutput {

    private final List<ReservationExport> reservations;
    private final List<String> ticketCategories;
    private final List<String> extraCategories;
    private final boolean restricted;

    private CsvResponse(List<ReservationExport> reservations,
            List<String> ticketCategories,
            List<String> extraCategories,
            boolean restricted) {

      this.reservations = reservations;
      this.ticketCategories = ticketCategories;
      this.extraCategories = extraCategories;
      this.restricted = restricted;
    }

    @Override
    public void write(OutputStream out) throws IOException, WebApplicationException {

      try (PrintWriter writer = new PrintWriter(out); CsvListWriter csvWriter = new CsvListWriter(
              writer, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)) {

        List<Object> line = new LinkedList(List.of("Datum", "Uhrzeit", "Veranstaltung",
                "Spielstätte", "Ort", "Saal", "ist Unterveranstaltung", "Institution",
                "Vorname", "Nachname"));

        if (!restricted) {
          line.addAll(List.of("Adresse", "PLZ", "Ort", "Telefon", "E-Mail"));
          for (String category : extraCategories) {
            line.add(category);
          }
        }

        line.add("Status");

        for (String c : ticketCategories) {
          line.add(c);
        }

        line.add("Nachricht");

        csvWriter.write(line);

        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM)
                .localizedBy(Locale.GERMANY);

        DateTimeFormatter timeFormatter = DateTimeFormatter
                .ofLocalizedTime(FormatStyle.MEDIUM)
                .localizedBy(Locale.GERMANY);

        for (ReservationExport r : reservations) {
          line.clear();
          Reservation currentReservation = r.getReservation();

          line.add(currentReservation.getEvent().getDate().format(dateFormatter));
          line.add(currentReservation.getEvent().getTime().format(timeFormatter));
          line.add(currentReservation.getEvent().getProduction().getFullName());
          line.add(currentReservation.getEvent().getHall().getVenue().getName());
          line.add(currentReservation.getEvent().getHall().getVenue().getCity());
          line.add(currentReservation.getEvent().getHall().getName());
          line.add(currentReservation.getParentReservation() == null ? "nein" : "ja");
          line.add(currentReservation.getCompany());
          line.add(currentReservation.getGivenName());
          line.add(currentReservation.getSurname());

          if (!restricted) {
            line.add(currentReservation.getAddress());
            line.add(currentReservation.getPostcode());
            line.add(currentReservation.getCity());
            line.add(currentReservation.getPhone());
            line.add(currentReservation.getEmail());

            for (String c : extraCategories) {
              line.add(r.getExtras().get(c));
            }
          }

          line.add(currentReservation.getStatus().toString());

          for (String c : ticketCategories) {
            line.add(r.getTickets().get(c));
          }

          line.add(currentReservation.getMessage());

          csvWriter.write(line);
        }
      }
    }
  }
}
