package app.espai.views.venues;

import app.espai.dao.Attachments;
import app.espai.dao.EventTicketPriceTemplates;
import app.espai.dao.HallCapacities;
import app.espai.dao.Halls;
import app.espai.dao.VenueContacts;
import app.espai.dao.Venues;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.EventTicketPriceTemplateFilter;
import app.espai.filter.HallCapacityFilter;
import app.espai.filter.HallFilter;
import app.espai.filter.VenueContactFilter;
import app.espai.model.Attachment;
import app.espai.model.AttachmentType;
import app.espai.model.EventTicketPriceTemplate;
import app.espai.model.Hall;
import app.espai.model.HallCapacity;
import app.espai.model.SeatCategory;
import app.espai.model.Venue;
import app.espai.model.VenueContact;
import app.espai.views.BaseView;
import app.espai.views.Dialog;
import app.espai.views.UserContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import rocks.xprs.runtime.db.PageableFilter;
import rocks.xprs.runtime.exceptions.AccessDeniedException;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class VenueDetailsView extends BaseView {

  @EJB
  private UserContext userContext;

  @EJB
  private Venues venues;

  @EJB
  private VenueContacts venueContacts;

  @EJB
  private Attachments attachments;

  @EJB
  private Halls halls;

  @EJB
  private HallCapacities hallCapacities;

  @EJB
  private EventTicketPriceTemplates priceTemplates;

  private Venue venue;
  private List<Attachment> imageList;
  private List<Hall> hallList;
  private List<VenueContact> venueContactList;
  private Map<Long, List<HallCapacity>> hallCapacityMap = new HashMap<>();
  private List<SeatCategory> seatCategoryList;
  private List<EventTicketPriceTemplate> priceTemplateList;

  @PostConstruct
  public void init() {

    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String venueIdParam = econtext.getRequestParameterMap().get("venueId");
    if (venueIdParam == null || !venueIdParam.matches("\\d+")) {
      throw new ResourceNotFoundException("venueId missing");
    }

    venue = venues.get(Long.parseLong(venueIdParam));

    if (userContext.isRestricted() && !userContext.getVenues().contains(venue)) {
      throw new AccessDeniedException("Access denied");
    }

    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityType(Venue.class.getSimpleName());
    attachmentFilter.setEntityId(venue.getId());
    attachmentFilter.setMediaType(AttachmentType.IMAGE);
    attachmentFilter.setOrderBy("position");
    attachmentFilter.setOrder(PageableFilter.Order.ASC);
    imageList = attachments.list(attachmentFilter).getItems();

    VenueContactFilter contactFilter = new VenueContactFilter();
    contactFilter.setVenue(venue);
    contactFilter.setOrderBy("familyName");
    contactFilter.setOrder(PageableFilter.Order.ASC);
    venueContactList = venueContacts.list(contactFilter).getItems();
    venueContactList.sort(VenueContacts.DEFAULT_ORDER);

    HallFilter hallFilter = new HallFilter();
    hallFilter.setVenue(venue);
    hallFilter.setOrderBy("name");
    hallFilter.setOrder(PageableFilter.Order.ASC);
    hallList = halls.list(hallFilter).getItems();
    
    HallCapacityFilter capacityFilter = new HallCapacityFilter();
    capacityFilter.setHalls(hallList);
    hallCapacityMap = hallCapacities.list(capacityFilter).getItems().stream()
            .collect(Collectors.groupingBy(hc -> hc.getHall().getId()));

    EventTicketPriceTemplateFilter priceTemplateFilter = new EventTicketPriceTemplateFilter();
    priceTemplateFilter.setVenue(venue);
    priceTemplateList = priceTemplates.list(priceTemplateFilter).getItems();
  }

  public void editVenue() {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("venueId", Arrays.asList(String.valueOf(venue.getId())));
    PrimeFaces.current().dialog().openDynamic(
            "editor.xhtml",
            Dialog.getDefaultOptions(600, 660),
            params);
  }

  public void removeVenue() {
    venues.delete(venue);
    redirect("index.xhtml", "Spielstätte gelöscht", null, FacesMessage.SEVERITY_INFO);
  }

  public void addContact() {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("venueId", Arrays.asList(String.valueOf(venue.getId())));
    PrimeFaces.current().dialog().openDynamic(
            "contacts/editor.xhtml",
            Dialog.getDefaultOptions(600, 650),
            params);
  }

  public void editContact(long contactId) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("venueId", Arrays.asList(String.valueOf(venue.getId())));
    params.put("contactId", Arrays.asList(String.valueOf(contactId)));
    PrimeFaces.current().dialog().openDynamic(
            "contacts/editor.xhtml",
            Dialog.getDefaultOptions(600, 650),
            params);
  }

  public void removeContact(long contactId) {
    VenueContact contact = venueContacts.get(contactId);
    venueContacts.delete(contact);
    init();
    FacesContext.getCurrentInstance().addMessage(
            null, new FacesMessage("Ansprechpartner gelöscht."));
  }

  public void addHall() {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("venueId", Arrays.asList(String.valueOf(venue.getId())));
    PrimeFaces.current().dialog().openDynamic(
            "halls/editor",
            Dialog.getDefaultOptions(600, 800),
            params);
  }

  public void editHall(long hallId) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("venueId", Arrays.asList(String.valueOf(venue.getId())));
    params.put("hallId", Arrays.asList(String.valueOf(hallId)));
    PrimeFaces.current().dialog().openDynamic(
            "halls/editor",
            Dialog.getDefaultOptions(600, 800),
            params);
  }

  public void removeHall(long hallId) {
    Hall hall = halls.get(hallId);
    halls.delete(hall);
    init();
    FacesContext.getCurrentInstance().addMessage(
            null, new FacesMessage("Saal gelöscht."));
  }

  public void addHallCapacity(long hallId) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("hallId", Arrays.asList(String.valueOf(hallId)));

    PrimeFaces.current().dialog().openDynamic(
            "/catalogs/venues/halls/hallCapacityEditor",
            Dialog.getDefaultOptions(600, 500),
            params);
  }

  public void editHallCapacity(long hallCapacityId) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("hallCapacityId", Arrays.asList(String.valueOf(hallCapacityId)));

    PrimeFaces.current().dialog().openDynamic(
            "/catalogs/venues/halls/hallCapacityEditor",
            Dialog.getDefaultOptions(600, 500),
            params);
  }

  public void removeHallCapacity(long seatCategoryId) {
    HallCapacity hallCapacity = hallCapacities.get(seatCategoryId);
    hallCapacities.delete(hallCapacity);
    init();
    FacesContext.getCurrentInstance().addMessage(
            null, new FacesMessage("Platzkategorie gelöscht."));
  }

  public void addPriceTemplate() {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("venueId", Arrays.asList(String.valueOf(venue.getId())));

    PrimeFaces.current().dialog().openDynamic(
            "/catalogs/venues/priceTemplates/editor",
            Dialog.getDefaultOptions(500, 500),
            params);
  }

  public void editPriceTemplate(long templateId) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("priceTemplateId", Arrays.asList(String.valueOf(templateId)));

    PrimeFaces.current().dialog().openDynamic(
            "/catalogs/venues/priceTemplates/editor",
            Dialog.getDefaultOptions(500, 500),
            params);
  }

  public void removePriceTemplate(long templateId) {
    EventTicketPriceTemplate priceTemplate = priceTemplates.get(templateId);
    priceTemplates.delete(priceTemplate);
    init();
    FacesContext.getCurrentInstance().addMessage(
            null, new FacesMessage("Preis gelöscht."));
  }

  public void addImage() {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("entityId", Arrays.asList(String.valueOf(venue.getId())));
    params.put("entityType", Arrays.asList(Venue.class.getSimpleName()));
    params.put("mediaType", Arrays.asList(AttachmentType.IMAGE.toString()));
    params.put("category", Arrays.asList("Profilbild"));
    PrimeFaces.current().dialog().openDynamic(
            "/catalogs/attachments/editor",
            Dialog.getDefaultOptions(600, 500),
            params);
  }

  public void editImage(long attachmentId) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("attachmentId", Arrays.asList(String.valueOf(attachmentId)));
    PrimeFaces.current().dialog().openDynamic(
            "/catalogs/attachments/editor",
            Dialog.getDefaultOptions(600, 500),
            params);
  }

  public void removeImage(long attachmentId) {
    Attachment attachment = attachments.get(attachmentId);
    attachments.delete(attachment);
    init();
    FacesContext.getCurrentInstance().addMessage(
            null, new FacesMessage("Bild gelöscht."));
  }

  public void onDataChanged(SelectEvent<Object> event) {
    init();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the venue
   */
  public Venue getVenue() {
    return venue;
  }

  /**
   * @param venue the venue to set
   */
  public void setVenue(Venue venue) {
    this.venue = venue;
  }

  /**
   * @return the imageList
   */
  public List<Attachment> getImageList() {
    return imageList;
  }

  /**
   * @param imageList the imageList to set
   */
  public void setImageList(List<Attachment> imageList) {
    this.imageList = imageList;
  }

  /**
   * @return the hallList
   */
  public List<Hall> getHallList() {
    return hallList;
  }

  /**
   * @param hallList the hallList to set
   */
  public void setHallList(List<Hall> hallList) {
    this.hallList = hallList;
  }

  /**
   * @return the venueContactList
   */
  public List<VenueContact> getVenueContactList() {
    return venueContactList;
  }

  /**
   * @param venueContactList the venueContactList to set
   */
  public void setVenueContactList(List<VenueContact> venueContactList) {
    this.venueContactList = venueContactList;
  }

  /**
   * @return the hallCapacityMap
   */
  public Map<Long, List<HallCapacity>> getHallCapacityMap() {
    return hallCapacityMap;
  }

  /**
   * @param hallCapacityMap the hallCapacityMap to set
   */
  public void setHallCapacityMap(Map<Long, List<HallCapacity>> hallCapacityMap) {
    this.hallCapacityMap = hallCapacityMap;
  }

  /**
   * @return the seatCategoryList
   */
  public List<SeatCategory> getSeatCategoryList() {
    return seatCategoryList;
  }

  /**
   * @param seatCategoryList the seatCategoryList to set
   */
  public void setSeatCategoryList(List<SeatCategory> seatCategoryList) {
    this.seatCategoryList = seatCategoryList;
  }

  /**
   * @return the priceTemplateList
   */
  public List<EventTicketPriceTemplate> getPriceTemplateList() {
    return priceTemplateList;
  }

  /**
   * @param priceTemplateList the priceTemplateList to set
   */
  public void setPriceTemplateList(List<EventTicketPriceTemplate> priceTemplateList) {
    this.priceTemplateList = priceTemplateList;
  }
  //</editor-fold>
}
