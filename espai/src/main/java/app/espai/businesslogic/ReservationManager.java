/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.businesslogic;

import app.espai.EspaiMail;
import app.espai.Mailer;
import app.espai.ReservationMailManager;
import app.espai.dao.Reservations;
import app.espai.model.Reservation;
import app.espai.model.ReservationStatus;
import app.espai.model.ReservationSummary;
import app.espai.views.reservations.ReservationIndexView;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rborowski
 */
@Stateless
public class ReservationManager {

  @EJB
  private Reservations reservations;

  @EJB
  private ReservationMailManager reservationMailManager;
  
  public void confirmReservationSummaries(List<ReservationSummary> selectedReservations) {
    confirmReservations(toReservations(selectedReservations));
  }

  public void confirmReservations(List<Reservation> selectedReservations) {
    int counter = 0;

    for (Reservation r : selectedReservations) {
      r.setStatus(ReservationStatus.CONFIRMED);
      reservations.save(r);

      try {
        EspaiMail mail = reservationMailManager.createConfirmed(r);

        Mailer mailer = new Mailer(r.getEvent().getSeason().getMailAccount());
        Message message = mailer.send(mail);
        mailer.saveSent(message);
        counter++;
      } catch (MessagingException | IOException ex) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "E-Mail für Buchungen %d nicht gesendet.".formatted(r.getId()), null));
        Logger.getLogger(ReservationIndexView.class.getName()).log(
                Level.WARNING,
                "Failed to send confirmation message for reservation no. %d".formatted(r.getId()),
                ex);
      }
    }

    FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("%d Buchungen bestätigt.".formatted(counter)));
  }
  
  public void holdReservationSummaries(List<ReservationSummary> selectedReservations) {
    holdReservations(toReservations(selectedReservations));
  }

  public void holdReservations(List<Reservation> selectedReservations) {
    int counter = 0;
    for (Reservation r : selectedReservations) {
      r.setStatus(ReservationStatus.HOLD);
      reservations.save(r);

      try {
        EspaiMail mail = reservationMailManager.createHold(r);

        Mailer mailer = new Mailer(r.getEvent().getSeason().getMailAccount());
        Message message = mailer.send(mail);
        mailer.saveSent(message);
        counter++;
      } catch (MessagingException | IOException ex) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "E-Mail für Buchungen %d nicht gesendet.".formatted(r.getId()), null));
        Logger.getLogger(ReservationIndexView.class.getName()).log(
                Level.WARNING,
                "Failed to send confirmation message for reservation no. %d".formatted(r.getId()),
                ex);
      }
    }
    
    FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("%d Buchungen geparkt.".formatted(counter)));
  }
  
  public void cancelReservationSummaries(List<ReservationSummary> selectedReservations) {
    cancelReservations(toReservations(selectedReservations));
  }

  public void cancelReservations(List<Reservation> selectedReservations) {
    int counter = 0;
    for (Reservation r : selectedReservations) {
      
      r.setStatus(ReservationStatus.CANCELED);
      reservations.save(r);

      try {
        EspaiMail mail = reservationMailManager.createCanceled(r);

        Mailer mailer = new Mailer(r.getEvent().getSeason().getMailAccount());
        Message message = mailer.send(mail);
        mailer.saveSent(message);
        counter++;
      } catch (MessagingException | IOException ex) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "E-Mail für Buchungen %d nicht gesendet.".formatted(r.getId()), null));
        Logger.getLogger(ReservationIndexView.class.getName()).log(
                Level.WARNING,
                "Failed to send confirmation message for reservation no. %d".formatted(r.getId()),
                ex);
      }
    }
    
    FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Buchungen storniert.".formatted(counter)));
  }
  
  private List<Reservation> toReservations(List<ReservationSummary> reservationSummaries) {
    return reservationSummaries.stream()
            .map(r -> reservations.get(r.getId()))
            .toList();
  }

}
