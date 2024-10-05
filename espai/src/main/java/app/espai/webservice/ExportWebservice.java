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
import app.espai.model.Reservation;
import app.espai.model.ReservationExtra;
import app.espai.model.ReservationTicket;
import app.espai.model.Season;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    List<ReservationExtra> extralist = extras.list(extraFilter).getItems();

    Map<Long, List<ReservationTicket>> ticketMap = new HashMap<>();
    List<String> ticketCategories = new LinkedList<>();
    ticketList.forEach(t -> {
      if (!ticketMap.containsKey(t.getReservation().getId())) {
        ticketMap.put(t.getReservation().getId(), new LinkedList<>());
      }
      ticketMap.get(t.getReservation().getId()).add(t);

      if (!ticketCategories.contains(t.getPriceCategory().getName())) {
        ticketCategories.add(t.getPriceCategory().getName());
      }
    });

    Map<Long, Map<String, String>> extraMap = new HashMap<>();
    List<String> extraCategories = new LinkedList<>();
    extralist.forEach(e -> {
      if (!extraMap.containsKey(e.getReservation().getId())) {
        extraMap.put(e.getReservation().getId(), new HashMap<String, String>());
      }
      extraMap.get(e.getReservation().getId()).put(e.getFieldName(), e.getValue());

      if (!extraCategories.contains(e.getFieldName())) {
        extraCategories.add(e.getFieldName());
      }
    });

    return Response
            .ok(
                    new CsvResponse(
                            reservationList,
                            ticketMap,
                            ticketCategories,
                            extraMap,
                            extraCategories,
                            userContext.isRestricted()),
                    "application/csv")
            .header("Content-Disposition",
                    "attachment; filename=\"Reservierungen " + season.getName() + ".csv\"")
            .build();
  }

  private static class CsvResponse implements StreamingOutput {

    private final List<Reservation> reservations;
    private final Map<Long, List<ReservationTicket>> ticketMap;
    private final List<String> ticketCategories;
    private final Map<Long, Map<String, String>> extraMap;
    private final List<String> extraCategories;
    private final boolean restricted;

    private CsvResponse(List<Reservation> reservations,
            Map<Long, List<ReservationTicket>> ticketMap,
            List<String> ticketCategories,
            Map<Long, Map<String, String>> extraMap,
            List<String> extraCategories,
            boolean restricted) {

      this.reservations = reservations;
      this.ticketMap = ticketMap;
      this.ticketCategories = ticketCategories;
      this.extraMap = extraMap;
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

        ArrayList<Integer> tickets = new ArrayList<>();

        for (Reservation r : reservations) {
          line.clear();

          line.add(r.getEvent().getDate().format(dateFormatter));
          line.add(r.getEvent().getTime().format(timeFormatter));
          line.add(r.getEvent().getProduction().getFullName());
          line.add(r.getEvent().getHall().getVenue().getName());
          line.add(r.getEvent().getHall().getVenue().getCity());
          line.add(r.getEvent().getHall().getName());
          line.add(r.getParentReservation() == null ? "nein" : "ja");
          line.add(r.getCompany());
          line.add(r.getGivenName());
          line.add(r.getSurname());

          if (!restricted) {
            line.add(r.getAddress());
            line.add(r.getPostcode());
            line.add(r.getCity());
            line.add(r.getPhone());
            line.add(r.getEmail());

            for (String category : extraCategories) {
              if (extraMap.containsKey(r.getId()) && extraMap.get(r.getId()).containsKey(category)) {
                line.add(extraMap.get(r.getId()).get(category));
              } else {
                line.add("");
              }
            }
          }

          line.add(r.getStatus().toString());

          tickets.clear();
          ticketCategories.forEach(t -> tickets.add(0));
          List<ReservationTicket> currentTickets = ticketMap.get(r.getId());
          if (currentTickets != null) {
            currentTickets.forEach(t -> {
              tickets.set(ticketCategories.indexOf(t.getPriceCategory().getName()), t.getAmount());
            });
          }

          line.addAll(tickets);

          line.add(r.getMessage());

          csvWriter.write(line);
        }
      }
    }
  }
}
