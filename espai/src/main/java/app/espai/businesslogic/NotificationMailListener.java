/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.businesslogic;

import app.espai.Mailer;
import app.espai.ReservationMailManager;
import app.espai.dao.MailTemplates;
import app.espai.dao.VenueContacts;
import app.espai.events.ReservationChangedEvent;
import app.espai.filter.VenueContactFilter;
import app.espai.model.MailTemplate;
import app.espai.model.Reservation;
import app.espai.model.ReservationStatus;
import app.espai.model.VenueContact;
import app.espai.webservice.ReservationWebservice;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rocks.xprs.mail.Mail;

/**
 *
 * @author rborowski
 */
@Stateless
public class NotificationMailListener {

  @EJB
  private VenueContacts venueContacts;

  @EJB
  private MailTemplates mailTemplates;

  @EJB
  private ReservationMailManager mailManager;

  public void notifyCustomer(@ObservesAsync ReservationChangedEvent event) {
    if (event.getReservation().getEmail() == null || event.getReservation().getEmail().isBlank()) {
      return;
    }
    
    if (event.getOldStatus() == ReservationStatus.UNSET
            && event.getNewStatus() == ReservationStatus.NEW) {

      try {
        Mailer mailer = new Mailer(event.getReservation().getEvent().getSeason().getMailAccount());
        Mail mail = mailManager.createNew(event.getReservation());
        Message message = mailer.send(mail);
        mailer.saveSent(message);
      } catch (IOException | MessagingException ex) {
        Logger.getLogger(ReservationWebservice.class.getName()).log(
                Level.SEVERE,
                String.format("Could not sent reservation_new mail for reservation %d",
                        event.getReservation().getId()), ex);
      }
    }
  }

  public void notifyContacts(@ObservesAsync ReservationChangedEvent event) {

    if (event.getOldStatus() == ReservationStatus.UNSET
            && event.getNewStatus() == ReservationStatus.NEW) {
      sendMessage(event.getReservation(), "NOTIFY_VENUE_NEW");
    } else if (event.getOldStatus() != ReservationStatus.CANCELED
            && event.getNewStatus() == ReservationStatus.CANCELED) {
      sendMessage(event.getReservation(), "NOTIFY_VENUE_CANCELED");
    }
  }

  private void sendMessage(Reservation reservation, String mailTemplateCode) {

    MailTemplate mailTemplate = mailTemplates.getByCode(
            mailTemplateCode, reservation.getEvent().getSeason());

    if (mailTemplate == null || mailTemplate.getHtmlMessage() == null
            || mailTemplate.getHtmlMessage().isBlank()) {

      return;
    }

    VenueContactFilter contactFilter = new VenueContactFilter();
    contactFilter.setVenue(reservation.getEvent().getHall().getVenue());
    contactFilter.setNotifyOnReservation(Boolean.TRUE);

    List<VenueContact> contactList = venueContacts.list(contactFilter).getItems();
    for (VenueContact c : contactList) {
      try {
        Mailer mailer = new Mailer(reservation.getEvent().getSeason().getMailAccount());
        Mail mail = mailManager.toMail(reservation, c.getGivenName(), c.getFamilyName(),
                c.getEmail(), mailTemplate);

        Message sentMessage = mailer.send(mail);
        mailer.saveSent(sentMessage);
      } catch (MessagingException | IOException ex) {
        Logger.getLogger(NotificationMailListener.class.getName()).log(
                Level.SEVERE,
                String.format("Could not send mail to Contact ID %d for Reservation ID %d.",
                        c.getId(), reservation.getId()),
                ex);
      }
    }
  }

}
