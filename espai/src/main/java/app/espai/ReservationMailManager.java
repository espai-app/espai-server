package app.espai;

import app.espai.dao.Attachments;
import app.espai.dao.MailTemplates;
import app.espai.dao.ReservationTickets;
import app.espai.dao.Reservations;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.ReservationFilter;
import app.espai.filter.ReservationTicketFilter;
import app.espai.model.Attachment;
import app.espai.model.Event;
import app.espai.model.MailTemplate;
import app.espai.model.Production;
import app.espai.model.Reservation;
import app.espai.model.ReservationTicket;
import app.espai.model.Venue;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.mail.internet.AddressException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rocks.xprs.mail.Mail;

/**
 *
 * @author rborowski
 */
@Stateless
public class ReservationMailManager {

  @EJB
  private Reservations reservations;

  @EJB
  private ReservationTickets reservationTickets;

  @EJB
  private Attachments attachments;

  @EJB
  private MailTemplates mailTemplates;

  public Mail createNew(Reservation reservation) {
    MailTemplate template = mailTemplates.getByCode(
            "RESERVATION_NEW", reservation.getEvent().getSeason());
    return toMail(reservation, reservation.getGivenName(), reservation.getSurname(),
            reservation.getEmail(), template);
  }

  public Mail createHold(Reservation reservation) {
    MailTemplate template = mailTemplates.getByCode(
            "RESERVATION_HOLD", reservation.getEvent().getSeason());
    return toMail(reservation, reservation.getGivenName(), reservation.getSurname(),
            reservation.getEmail(), template);
  }

  public Mail createConfirmed(Reservation reservation) {
    MailTemplate template = mailTemplates.getByCode(
            "RESERVATION_CONFIRMED", reservation.getEvent().getSeason());
    return toMail(reservation, reservation.getGivenName(), reservation.getSurname(),
            reservation.getEmail(), template);
  }

  public Mail createCanceled(Reservation reservation) {
    MailTemplate template = mailTemplates.getByCode(
            "RESERVATION_CANCELED", reservation.getEvent().getSeason());
    return toMail(reservation, reservation.getGivenName(), reservation.getSurname(),
            reservation.getEmail(), template);
  }

  public Mail toMail(Reservation reservation, String givenName, String surname, String email,
          MailTemplate template) {

    Event currentEvent = reservation.getEvent();
    Production currentProduction = reservation.getEvent().getProduction().getProduction();
    Venue currentVenue = reservation.getEvent().getHall().getVenue();

    ReservationFilter reservationFilter = new ReservationFilter();
    reservationFilter.setParentReservation(reservation);
    List<Reservation> childReservations = reservations.list(reservationFilter).getItems();

    ReservationTicketFilter ticketFilter = new ReservationTicketFilter();
    ticketFilter.setReservation(reservation);
    List<ReservationTicket> tickets = reservationTickets.list(ticketFilter).getItems();

    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityType(currentProduction.getClass().getSimpleName());
    attachmentFilter.setEntityId(currentProduction.getId());

    List<Attachment> attachmentList = attachments.list(attachmentFilter).getItems();
    Map<String, List<Attachment>> attachmentMap = new HashMap<>();
    for (Attachment a : attachmentList) {
      if (!attachmentMap.containsKey(a.getCategory())) {
        attachmentMap.put(a.getCategory(), new LinkedList<>());
      }
      attachmentMap.get(a.getCategory()).add(a);
    }

    Map<String, String> replacements = new HashMap<>();
    replacements.put("id", String.valueOf(reservation.getId()));
    replacements.put("givenName", givenName);
    replacements.put("surname", surname);
    replacements.put("date", currentEvent.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    replacements.put("time", currentEvent.getTime().format(DateTimeFormatter.ofPattern("HH:mm")));

    String title = currentProduction.getTitle();
    String versionName = reservation.getEvent().getProduction().getVersionName();
    if (versionName != null && !versionName.isBlank()) {
      title += " (" + versionName + ")";
    }
    replacements.put("title", escHtml(title));
    replacements.put("duration", String.format("%d min.",
            currentProduction.getDurationInMinutes()));

    replacements.put("customer.company", escHtml(reservation.getCompany()));
    replacements.put("customer.givenName", escHtml(reservation.getGivenName()));
    replacements.put("customer.surname", escHtml(reservation.getSurname()));

    replacements.put("venue.name", escHtml(currentVenue.getName()));
    replacements.put("venue.address", escHtml(currentVenue.getAddress()));
    replacements.put("venue.postcode", escHtml(currentVenue.getPostcode()));
    replacements.put("venue.city", escHtml(currentVenue.getCity()));

    // child event list
    if (!childReservations.isEmpty()) {
      StringBuilder childEventList = new StringBuilder("");
      childEventList.append("<ul>");
      for (Reservation r : childReservations) {
        currentProduction = r.getEvent().getProduction().getProduction();
        childEventList.append(String.format("<li>%s (%d min.)</li>",
                escHtml(currentProduction.getTitle()),
                currentProduction.getDurationInMinutes()));
      }
      childEventList.append("</ul>");
      replacements.put("additionalEvents", childEventList.toString());
    } else {
      replacements.put("additionalEvents",  "<p>Keine Zusatzangebote gebucht.</p>");
    }


    // create ticket table
    StringBuilder ticketTable = new StringBuilder(
            "<ul>");
    for (ReservationTicket t : tickets) {
      ticketTable.append(String.format("<li>%d x %s (%.2f € pro Person)</li>",
              t.getAmount(),
              escHtml(t.getPriceCategory().getName()),
              t.getPrice().getAmount()
      ));
    }
    ticketTable.append("</ul>");
    replacements.put("tickets", ticketTable.toString());

    // create attachment lists
    for (Map.Entry<String, List<Attachment>> i : attachmentMap.entrySet()) {
      String renderedList = "";
      if (!i.getValue().isEmpty()) {
        StringBuilder aBuilder = new StringBuilder("<ul>");
        for (Attachment at : i.getValue()) {
          aBuilder.append("<li><a href=\"").append(escAttr(at.getLocation())).append("\">")
                  .append(escHtml(at.getCaption())).append("</a></li>");
        }
        aBuilder.append("</ul>");
        renderedList = aBuilder.toString();
      }
      replacements.put("attachments." + i.getKey(), renderedList);
    }

    Mail result = new Mail();
    result.setFrom(template.getSender());
    result.addTo(email);
    if (template.getBcc() != null && !template.getBcc().isBlank()) {
      String[] bccs = template.getBcc().split(",");
      for (String b : bccs) {
        try {
          result.addBcc(b.trim());
        } catch (AddressException ex) {
          Logger.getLogger(ReservationMailManager.class.getName()).log(
                  Level.INFO,
                  String.format("Could not add BCC %s to mail with template ID %d",
                          b, template.getId()),
                  ex);
        }
      }
    }
    result.setSubject(apply(template.getSubject(), replacements));
    result.setHtml(apply(template.getHtmlMessage(), replacements));
    return result;
  }

  private String apply(String templateString, Map<String, String> replacements) {
    String result = templateString;
    for (Map.Entry<String,String> r : replacements.entrySet()) {
      result = result.replace("{" + r.getKey() + "}", r.getValue());
    }
    return result;
  }

  private String escHtml(String attribute) {
    if (attribute == null) {
      return "";
    }

    return attribute;
//
//    return attribute.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

  private String escAttr(String attribute) {
    if (attribute == null) {
      return "";
    }

    return attribute.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&apos;")
            .replace("<", "&lt;").replace(">", "&gt;");
  }

}
