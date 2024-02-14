package app.espai.views.venues;

import app.espai.dao.VenueContacts;
import app.espai.dao.Venues;
import app.espai.model.Venue;
import app.espai.model.VenueContact;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import rocks.xprs.runtime.exceptions.InvalidDataException;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class VenueContactEditorView {

  @EJB
  private Venues venues;

  @EJB
  private VenueContacts contacts;

  private VenueContact contact;

  @PostConstruct
  public void init() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String contactIdParam = econtext.getRequestParameterMap().get("contactId");
    if (contactIdParam == null || !contactIdParam.matches("\\d+")) {

      String venueIdParam = econtext.getRequestParameterMap().get("venueId");
      if (venueIdParam == null || !venueIdParam.matches("\\d+")) {
        throw new InvalidDataException("venueId missing.");
      }

      Venue venue = venues.get(Long.parseLong(venueIdParam));
      if (venue == null) {
        throw new ResourceNotFoundException("Venue not found.");
      }

      contact = new VenueContact();
      contact.setVenue(venue);
    } else {
      contact = contacts.get(Long.parseLong(contactIdParam));
    }
  }

  public void save() {
    contact = contacts.save(contact);
    PrimeFaces.current().dialog().closeDynamic(contact);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the contact
   */
  public VenueContact getContact() {
    return contact;
  }

  /**
   * @param contact the contact to set
   */
  public void setContact(VenueContact contact) {
    this.contact = contact;
  }
  //</editor-fold>
}
