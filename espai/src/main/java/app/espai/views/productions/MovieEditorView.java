package app.espai.views.productions;

import app.espai.Constants;
import app.espai.dao.Agencies;
import app.espai.dao.AttachmentCategories;
import app.espai.dao.Attachments;
import app.espai.dao.Movies;
import app.espai.dao.ProductionTagTemplates;
import app.espai.dao.ProductionTags;
import app.espai.dao.ProductionVersions;
import app.espai.filter.AgencyFilter;
import app.espai.filter.AttachmentCategoryFilter;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.ProductionTagFilter;
import app.espai.filter.ProductionVersionFilter;
import app.espai.model.Agency;
import app.espai.model.Attachment;
import app.espai.model.AttachmentCategory;
import app.espai.model.Movie;
import app.espai.model.ProductionTag;
import app.espai.model.ProductionTagTemplate;
import app.espai.model.ProductionVersion;
import app.espai.views.BaseView;
import app.espai.views.Dialog;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import rocks.xprs.runtime.db.PageableFilter;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
@Named
@Stateful
@ViewScoped
@RolesAllowed({"MANAGER"})
public class MovieEditorView extends BaseView implements Serializable {

  @EJB
  private Movies movies;

  @EJB
  private ProductionVersions productionVersions;

  @EJB
  private ProductionTags tags;

  @EJB
  private ProductionTagTemplates tagTemplates;

  @EJB
  private Agencies agencies;

  @EJB
  private AttachmentCategories attachmentCategories;

  @EJB
  private Attachments attachments;

  private Movie movie;
  private List<ProductionVersion> versionList;
  private List<Agency> agencyList;
  private List<? extends ProductionTagTemplate> tagTemplateList;
  private Map<String, AutoCompleteResolver> tagTemplateMap;
  private List<ProductionTag> productionTagList;
  private Map<String, ListWrapper<String>> categorizedTags;
  private List<AttachmentCategory> attachmentCategoryList;
  private Map<String, ListWrapper<Attachment>> categorizedAttachments;

  @PostConstruct
  public void init() {

    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    // get list of agencies for drop down
    AgencyFilter agencyFilter = new AgencyFilter();
    agencyFilter.setOrderBy("name");
    agencyFilter.setOrder(PageableFilter.Order.ASC);
    agencyList = agencies.list(agencyFilter).getItems();

    // get list of tag templates
    tagTemplateList = tagTemplates.list().getItems();
    tagTemplateMap = new LinkedHashMap<>();
    categorizedTags = new LinkedHashMap<>();
    for (ProductionTagTemplate t : tagTemplateList) {
      tagTemplateMap.put(t.getName(), new AutoCompleteResolver(Arrays.asList(
              t.getPresets().split("\n"))
              .stream()
              .map(s -> s.trim())
              .collect(Collectors.toList())));

      categorizedTags.put(t.getName(), new ListWrapper(new LinkedList<>()));
    }

    // get list of attachment categories
    AttachmentCategoryFilter attachmentCategoryFilter = new AttachmentCategoryFilter();
    attachmentCategoryFilter.setEntityTypeIsNull(true);
    attachmentCategoryList = new LinkedList<>();
    attachmentCategoryList.addAll(attachmentCategories.list(attachmentCategoryFilter).getItems());

    attachmentCategoryFilter.setEntityType(Movie.class.getSimpleName());
    attachmentCategoryList.addAll(attachmentCategories.list(attachmentCategoryFilter).getItems());

    categorizedAttachments = new LinkedHashMap<>();
    for (AttachmentCategory a : attachmentCategoryList) {
      categorizedAttachments.put(a.getName(), new ListWrapper<>(new LinkedList<>()));
    }

    // create or open movie
    String movieParam = econtext.getRequestParameterMap().get(Constants.PRODUCTION_ID);
    if (movieParam != null) {
      movie = movies.get(Long.parseLong(movieParam));

      if (movie == null) {
        throw new ResourceNotFoundException("Film nicht gefunden");
      }

      // get production versions
      ProductionVersionFilter versionFilter = new ProductionVersionFilter();
      versionFilter.setProduction(movie);
      versionList = productionVersions.list(versionFilter).getItems();
      if (versionList.isEmpty()) {
        versionList = new LinkedList<>();
      }

      // get tags
      ProductionTagFilter tagFilter = new ProductionTagFilter();
      tagFilter.setOrderBy("position");
      tagFilter.setOrder(PageableFilter.Order.ASC);
      tagFilter.setProduction(movie);
      productionTagList = tags.list(tagFilter).getItems();
      productionTagList.forEach(t -> {
        if (categorizedTags.containsKey(t.getCategory())) {
          categorizedTags.get(t.getCategory()).getList().add(t.getValue());
        }
      });

      // get attachments
      onAttachmentsChanged(null);
    } else {
      movie = new Movie();
      movie.setFixedPrice(new Monetary(BigDecimal.ZERO, "EUR"));
      movie.setAdditionalCost(new Monetary(BigDecimal.ZERO, "EUR"));
      versionList = new LinkedList<>();
      productionTagList = new LinkedList<>();
    }
  }

  public void addProductionVersion() {
    versionList.add(new ProductionVersion());
  }

  public void removeProductionVersion(int index) {
    if (index < 0 || index >= versionList.size()) {
      return;
    }

    ProductionVersion v = versionList.get(index);
    if (v.getId() != null) {
      productionVersions.delete(v);
    }
    versionList.remove(index);
  }

  public void save() {
    movie = movies.save(movie);

    for (ProductionVersion v : versionList) {
      v.setProduction(movie);
      productionVersions.save(v);
    }

    // save production tags
    int tagCounter = 0;
    int positionCounter;
    int countExistingTags = productionTagList.size();
    for (String category : categorizedTags.keySet()) {
      positionCounter = 0;
      for (String tag : categorizedTags.get(category).getList()) {
        ProductionTag p;
        if (countExistingTags <= tagCounter) {
          p = new ProductionTag();
          p.setProduction(movie);
        } else {
          p = productionTagList.get(tagCounter);
        }
        p.setCategory(category);
        p.setValue(tag);
        p.setPosition(positionCounter);

        tags.save(p);
        tagCounter++;
      }
    }

    // delete tags that are too much
    if (tagCounter < countExistingTags) {
      for (; tagCounter < countExistingTags; tagCounter++) {
        tags.delete(productionTagList.get(tagCounter));
      }
    }

    redirect("editor.xhtml?" + Constants.PRODUCTION_ID + "=" + movie.getId(),
            "Film gespeichert.",
            "Film gespeichert.",
            FacesMessage.SEVERITY_INFO);
  }

  public void addAttachment(String category) {

    Optional<AttachmentCategory> currentCategory = attachmentCategoryList
            .stream()
            .filter(a -> a.getName().equals(category))
            .findFirst();

    if (currentCategory.isEmpty()) {
      return;
    }

    HashMap<String, List<String>> params = new HashMap<>();
    params.put("entityId", Arrays.asList(String.valueOf(movie.getId())));
    params.put("entityType", Arrays.asList(Movie.class.getSimpleName()));
    params.put("mediaType", Arrays.asList(currentCategory.get().getAttachmentType().toString()));
    params.put("category", Arrays.asList(category));
    PrimeFaces.current().dialog().openDynamic(
            "/catalogs/attachments/editor",
            Dialog.getDefaultOptions(600, 500),
            params);
  }

  public void editAttachment(long attachmentId) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("attachmentId", Arrays.asList(String.valueOf(attachmentId)));
    PrimeFaces.current().dialog().openDynamic(
            "/catalogs/attachments/editor",
            Dialog.getDefaultOptions(600, 500),
            params);
  }

  public void removeAttachment(long attachmentId) {
    Attachment attachment = attachments.get(attachmentId);
    attachments.delete(attachment);
    onAttachmentsChanged(null);
  }

  public void onAttachmentsChanged(SelectEvent<Object> event) {
    categorizedAttachments.clear();
    for (AttachmentCategory a : attachmentCategoryList) {
      categorizedAttachments.put(a.getName(), new ListWrapper<>(new LinkedList<>()));
    }

    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityType(Movie.class.getSimpleName());
    attachmentFilter.setEntityId(movie.getId());
    attachmentFilter.setOrderBy("position");
    attachmentFilter.setOrder(PageableFilter.Order.ASC);
    List<Attachment> attachmentList = attachments.list(attachmentFilter).getItems();
    attachmentList.forEach(a -> {
      if (categorizedAttachments.containsKey(a.getCategory())) {
        categorizedAttachments.get(a.getCategory()).getList().add(a);
      }
    });
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the movie
   */
  public Movie getMovie() {
    return movie;
  }

  /**
   * @param movie the movie to set
   */
  public void setMovie(Movie movie) {
    this.movie = movie;
  }

  /**
   * @return the versionList
   */
  public List<ProductionVersion> getVersionList() {
    return versionList;
  }

  /**
   * @param versionList the versionList to set
   */
  public void setVersionList(List<ProductionVersion> versionList) {
    this.versionList = versionList;
  }

  /**
   * @return the agencyList
   */
  public List<Agency> getAgencyList() {
    return agencyList;
  }

  /**
   * @param agencyList the agencyList to set
   */
  public void setAgencyList(List<Agency> agencyList) {
    this.agencyList = agencyList;
  }

  /**
   * @return the tagTemplateList
   */
  public List<? extends ProductionTagTemplate> getTagTemplateList() {
    return tagTemplateList;
  }

  /**
   * @param tagTemplateList the tagTemplateList to set
   */
  public void setTagTemplateList(List<? extends ProductionTagTemplate> tagTemplateList) {
    this.tagTemplateList = tagTemplateList;
  }

  /**
   * @return the tagTemplateMap
   */
  public Map<String, AutoCompleteResolver> getTagTemplateMap() {
    return tagTemplateMap;
  }

  /**
   * @param tagTemplateMap the tagTemplateMap to set
   */
  public void setTagTemplateMap(Map<String, AutoCompleteResolver> tagTemplateMap) {
    this.tagTemplateMap = tagTemplateMap;
  }

  /**
   * @return the categorizedTags
   */
  public Map<String, ListWrapper<String>> getCategorizedTags() {
    return categorizedTags;
  }

  /**
   * @param categorizedTags the categorizedTags to set
   */
  public void setCategorizedTags(Map<String, ListWrapper<String>> categorizedTags) {
    this.categorizedTags = categorizedTags;
  }

  /**
   * @return the attachmentCategoryList
   */
  public List<AttachmentCategory> getAttachmentCategoryList() {
    return attachmentCategoryList;
  }

  /**
   * @param attachmentCategoryList the attachmentCategoryList to set
   */
  public void setAttachmentCategoryList(List<AttachmentCategory> attachmentCategoryList) {
    this.attachmentCategoryList = attachmentCategoryList;
  }

  /**
   * @return the categorizedAttachments
   */
  public Map<String, ListWrapper<Attachment>> getCategorizedAttachments() {
    return categorizedAttachments;
  }

  /**
   * @param categorizedAttachments the categorizedAttachments to set
   */
  public void setCategorizedAttachments(Map<String, ListWrapper<Attachment>> categorizedAttachments) {
    this.categorizedAttachments = categorizedAttachments;
  }
  //</editor-fold>

  public static class AutoCompleteResolver implements Serializable {

    private final List<String> items;

    public AutoCompleteResolver(List<String> items) {
      this.items = items;
    }

    public List<String> lookup(String query) {
      query = query.toLowerCase();
      LinkedList<String> result = new LinkedList<>();
      for (String i : items) {
        if (i.toLowerCase().contains(query)) {
          result.add(i);
        }
      }

      result.sort(String.CASE_INSENSITIVE_ORDER);
      return result;
    }
  }

  public static class ListWrapper<T> {

    private List<T> list;

    public ListWrapper() {

    }

    public ListWrapper(List<T> list) {
      this.list = list;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    /**
     * @return the list
     */
    public List<T> getList() {
      return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<T> list) {
      this.list = list;
    }
    //</editor-fold>
  }
}
