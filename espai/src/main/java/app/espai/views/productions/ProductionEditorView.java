/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.productions;

import app.espai.RequestParameters;
import app.espai.dao.AttachmentCategories;
import app.espai.dao.Attachments;
import app.espai.dao.LicenseConditions;
import app.espai.dao.ProductionMetaFields;
import app.espai.dao.ProductionMetaValues;
import app.espai.dao.ProductionTypes;
import app.espai.dao.ProductionVersions;
import app.espai.dao.Productions;
import app.espai.filter.AttachmentCategoryFilter;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.LicenseConditionFilter;
import app.espai.filter.ProductionMetaFieldFilter;
import app.espai.filter.ProductionMetaValueFilter;
import app.espai.filter.ProductionVersionFilter;
import app.espai.model.Attachment;
import app.espai.model.AttachmentCategory;
import app.espai.model.LicenseCondition;
import app.espai.model.Production;
import app.espai.model.ProductionMetaField;
import app.espai.model.ProductionMetaValue;
import app.espai.model.ProductionTag;
import app.espai.model.ProductionType;
import app.espai.model.ProductionVersion;
import app.espai.views.BaseView;
import app.espai.views.Dialog;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.primefaces.PF;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import rocks.xprs.runtime.db.PageableFilter;

/**
 *
 * @author rborowski
 */
@Named
@ViewScoped
public class ProductionEditorView extends BaseView implements Serializable {

  @EJB
  private Productions productions;

  @EJB
  private ProductionTypes productionTypes;

  @EJB
  private ProductionMetaFields metaFields;

  @EJB
  private ProductionMetaValues metaValues;

  @EJB
  private LicenseConditions licenses;

  @EJB
  private ProductionVersions versions;

  @EJB
  private AttachmentCategories attachmentCategories;

  @EJB
  private Attachments attachments;

  private Production production;
  private List<MetaFieldHolder> metaFieldList;
  private List<LicenseCondition> licenseList;
  private List<ProductionVersion> versionList;

  private List<ProductionTag> productionTagList;
  private List<AttachmentCategory> attachmentCategoryList;
  private Map<String, ProductionEditorView.ListWrapper<Attachment>> categorizedAttachments;

  @PostConstruct
  public void init() {

    // get list of attachment categories
    AttachmentCategoryFilter attachmentCategoryFilter = new AttachmentCategoryFilter();
    attachmentCategoryFilter.setEntityTypeIsNull(true);
    attachmentCategoryList = new LinkedList<>();
    attachmentCategoryList.addAll(attachmentCategories.list(attachmentCategoryFilter).getItems());

    attachmentCategoryFilter.setEntityType(Production.class.getSimpleName());
    attachmentCategoryList.addAll(attachmentCategories.list(attachmentCategoryFilter).getItems());

    categorizedAttachments = new LinkedHashMap<>();
    for (AttachmentCategory a : attachmentCategoryList) {
      categorizedAttachments.put(a.getName(), new ProductionEditorView.ListWrapper<>(new LinkedList<>()));
    }

    Long productionId = RequestParameters.getParameterAsLong("productionId");
    if (productionId != null) {
      production = productions.get(productionId);

      if (production == null) {
        throw new NotFoundException("Production not found.");
      }

      LicenseConditionFilter licenseFilter = new LicenseConditionFilter();
      licenseFilter.setProduction(production);
      licenseList = new LinkedList<>(licenses.list(licenseFilter).getItems());

      ProductionVersionFilter versionFilter = new ProductionVersionFilter();
      versionFilter.setProduction(production);
      versionList = new LinkedList<>(versions.list(versionFilter).getItems());

      onAttachmentsChanged(null);

    } else {
      Long productionTypeId = RequestParameters.getParameterAsLong("productionTypeId");

      if (productionTypeId == null) {
        throw new BadRequestException("ProductionType missing.");
      }

      ProductionType type = productionTypes.get(productionTypeId);
      if (type == null) {
        throw new NotFoundException("ProductionType not found.");
      }

      production = new Production();
      production.setProductionType(type);

      licenseList = new LinkedList<>();
      versionList = new LinkedList<>();
    }

    ProductionMetaFieldFilter fieldFilter = new ProductionMetaFieldFilter();
    fieldFilter.setProductionType(production.getProductionType());
    fieldFilter.setOrderBy("position");
    fieldFilter.setOrder(PageableFilter.Order.ASC);

    List<ProductionMetaField> fieldList = metaFields.list(fieldFilter).getItems();
    metaFieldList = new LinkedList<>();

    for (ProductionMetaField field : fieldList) {
      ProductionMetaValueFilter valueFilter = new ProductionMetaValueFilter();
      valueFilter.setProduction(production);
      valueFilter.setProductionMetaField(field);
      valueFilter.setOrderBy("position");
      valueFilter.setOrder(PageableFilter.Order.ASC);

      List<ProductionMetaValue> metaValueList
              = new LinkedList<>(metaValues.list(valueFilter).getItems());

      ProductionMetaValue value;
      if (metaValueList.isEmpty()) {
        value = new ProductionMetaValue();
        value.setProduction(production);
        value.setProductionMetaField(field);
      } else {
        value = metaValueList.get(0);
      }

      metaFieldList.add(new MetaFieldHolder(field, value));
    }
  }

  public void save() {
    production = productions.save(production);

    for (MetaFieldHolder metaField : metaFieldList) {
      ProductionMetaValue value = metaField.getValue();
      value.setProduction(production);
      metaValues.save(value);
    }

    for (ProductionVersion version : versionList) {
      version.setProduction(production);
      versions.save(version);
    }

    for (LicenseCondition license : licenseList) {
      license.setProduction(production);
      licenses.save(license);
    }

    redirect(String.format("editor.xhtml?productionId=%d", production.getId()),
            "Gespeichert.",
            null,
            FacesMessage.SEVERITY_INFO);
  }

  public void addProductionVersion() {
    ProductionVersion version = new ProductionVersion();
    version.setProduction(production);
    versionList.add(version);
  }

  public void removeProductionVersion(int listIndex) {
    ProductionVersion deletedVersion = versionList.remove(listIndex);
    if (deletedVersion.getId() != null) {
      versions.delete(deletedVersion);
    }
  }

  public void addLicenseCondition() {
    LicenseCondition license = new LicenseCondition();
    license.setProduction(production);
    licenseList.add(license);
  }

  public void removeLicenseCondition(int listIndex) {
    LicenseCondition deletedLicense = licenseList.remove(listIndex);
    if (deletedLicense.getId() != null) {
      licenses.delete(deletedLicense);
    }
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
    params.put("entityId", Arrays.asList(String.valueOf(production.getId())));
    params.put("entityType", Arrays.asList(Production.class.getSimpleName()));
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
      categorizedAttachments.put(a.getName(), new ProductionEditorView.ListWrapper<>(new LinkedList<>()));
    }

    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityType(Production.class.getSimpleName());
    attachmentFilter.setEntityId(production.getId());
    attachmentFilter.setOrderBy("position");
    attachmentFilter.setOrder(PageableFilter.Order.ASC);
    List<Attachment> attachmentList = attachments.list(attachmentFilter).getItems();
    attachmentList.forEach(a -> {
      if (categorizedAttachments.containsKey(a.getCategory())) {
        categorizedAttachments.get(a.getCategory()).getList().add(a);
      }
    });
  }

  public void addLicense() {

    HashMap<String, List<String>> params = new HashMap<>();
    params.put("productionId", List.of(String.valueOf(production.getId())));

    PF.current().dialog().openDynamic(
            "/catalogs/productions/licenseEditDialog",
            Dialog.getDefaultOptions(600, 700),
            params);

  }

  public void editLicense(Long id) {
    HashMap<String, List<String>> params = new HashMap<>();
    params.put("licenseId", List.of(String.valueOf(id)));

    PF.current().dialog().openDynamic(
            "/catalogs/productions/licenseEditDialog",
            Dialog.getDefaultOptions(600, 700),
            params);
  }

  public void removeLicense(Long id) {

    if (id == null) {
      return;
    }

    LicenseCondition license = licenses.get(id);

    if (license != null) {
      licenses.delete(license);
    }
  }

  public void onLicensesChanged(SelectEvent<Object> event) {
    LicenseConditionFilter licenseFilter = new LicenseConditionFilter();
    licenseFilter.setProduction(production);
    licenseList = licenses.list(licenseFilter).getItems();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the production
   */
  public Production getProduction() {
    return production;
  }

  /**
   * @param production the production to set
   */
  public void setProduction(Production production) {
    this.production = production;
  }

  /**
   * @return the metaFieldList
   */
  public List<MetaFieldHolder> getMetaFieldList() {
    return metaFieldList;
  }

  /**
   * @param metaFieldList the metaFieldList to set
   */
  public void setMetaFieldList(List<MetaFieldHolder> metaFieldList) {
    this.metaFieldList = metaFieldList;
  }

  /**
   * @return the licenseList
   */
  public List<LicenseCondition> getLicenseList() {
    return licenseList;
  }

  /**
   * @param licenseList the licenseList to set
   */
  public void setLicenseList(List<LicenseCondition> licenseList) {
    this.licenseList = licenseList;
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
   * @return the productionTagList
   */
  public List<ProductionTag> getProductionTagList() {
    return productionTagList;
  }

  /**
   * @param productionTagList the productionTagList to set
   */
  public void setProductionTagList(List<ProductionTag> productionTagList) {
    this.productionTagList = productionTagList;
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
  public Map<String, ProductionEditorView.ListWrapper<Attachment>> getCategorizedAttachments() {
    return categorizedAttachments;
  }

  /**
   * @param categorizedAttachments the categorizedAttachments to set
   */
  public void setCategorizedAttachments(Map<String, ProductionEditorView.ListWrapper<Attachment>> categorizedAttachments) {
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

  public static class MetaFieldHolder {

    private ProductionMetaField field;
    private ProductionMetaValue value;
    private List<String> suggestions = new LinkedList<>();

    public MetaFieldHolder() {

    }

    public MetaFieldHolder(ProductionMetaField field, ProductionMetaValue value) {
      this.field = field;
      this.value = value;

      if (field.getPresets() != null && !field.getPresets().isBlank()) {
        suggestions = Arrays.asList(field.getPresets().split("\n"));
      }
    }

    public List<String> lookup(String query) {
      String lowercaseQuery = query.toLowerCase();

      LinkedList<String> result = new LinkedList<>();
      for (String i : suggestions) {
        if (i.toLowerCase().contains(lowercaseQuery)) {
          result.add(i);
        }
      }

      result.sort(String.CASE_INSENSITIVE_ORDER);
      result.addFirst(query);

      return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    /**
     * @return the field
     */
    public ProductionMetaField getField() {
      return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(ProductionMetaField field) {
      this.field = field;
    }

    /**
     * @return the value
     */
    public ProductionMetaValue getValue() {
      return value;
    }

    /**
     * @param value the values to set
     */
    public void setValues(ProductionMetaValue value) {
      this.value = value;
    }
    //</editor-fold>

  }
}
