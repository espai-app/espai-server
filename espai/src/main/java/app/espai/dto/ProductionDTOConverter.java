package app.espai.dto;

import app.espai.dao.Attachments;
import app.espai.dao.LicenseConditions;
import app.espai.dao.ProductionMetaValues;
import app.espai.dao.ProductionVersions;
import app.espai.dao.Productions;
import app.espai.filter.AttachmentFilter;
import app.espai.filter.LicenseConditionFilter;
import app.espai.filter.ProductionMetaValueFilter;
import app.espai.model.Attachment;
import app.espai.model.LicenseCondition;
import app.espai.model.Production;
import app.espai.model.ProductionMetaValue;
import app.espai.model.ProductionVersion;
import app.espai.sdk.model.ProductionDTO;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class ProductionDTOConverter {

  @EJB
  private Attachments attachments;

  @EJB
  private Productions productions;

  @EJB
  private ProductionMetaValues metaValues;

  @EJB
  private ProductionVersions versions;

  @EJB
  private LicenseConditions licenses;

  @Inject
  private ProductionTypeDTOConverter typeConverter;

  @Inject
  private AttachmentDTOConverter attachmentConverter;

  @Inject
  private ProductionMetaValueDTOConverter metaValueConverter;

  @Inject
  private ProductionVersionDTOConverter versionConverter;

  @Inject
  private LicenseConditionDTOConverter licenseConverter;


  public ProductionDTO toShortDTO(Production production) {

    if (production == null) {
      return null;
    }

    ProductionDTO result = new ProductionDTO();
    result.setId(production.getId());
    result.setProductionType(typeConverter.toShortDTO(production.getProductionType()));
    result.setTitle(production.getTitle());
    result.setSubtitle(production.getSubtitle());
    result.setDescription(production.getDescription());
    result.setDurationInMinutes(production.getDurationInMinutes());
    result.setRating(production.getRating());
    result.setRecommendedAge(production.getRecommendedAge());

    // add attachments
    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityType(Production.class.getSimpleName());
    attachmentFilter.setEntityId(production.getId());

    List<Attachment> attachmentList = attachments.list(attachmentFilter).getItems();

    result.setAttachments(attachmentList.stream()
            .map(a -> attachmentConverter.toDTO(a))
            .toList());

    return result;
  }

  public ProductionDTO toDTO(Production production) {

    ProductionDTO result = toShortDTO(production);

    if (result == null) {
      return null;
    }

    // add metadata
    ProductionMetaValueFilter metaValueFilter = new ProductionMetaValueFilter();
    metaValueFilter.setProduction(production);
    List<ProductionMetaValue> metaValueList = metaValues.list(metaValueFilter).getItems();
    result.setMetadata(metaValueList.stream().map(v -> metaValueConverter.toDTO(v)).toList());

    // add versions
    List<ProductionVersion> versionList = versions.list(production);
    result.setVersions(versionList.stream().map(v -> versionConverter.toDTO(v)).toList());

    // add licenses
    LicenseConditionFilter licenseFilter = new LicenseConditionFilter();
    licenseFilter.setProduction(production);

    List<LicenseCondition> licenseList = licenses.list(licenseFilter).getItems();
    result.setLicenseConditions(licenseList.stream().map(l -> licenseConverter.toDTO(l)).toList());

    return result;
  }

  public Production fromDTOReference(ProductionDTO reference) {

    Production result = null;
    if (reference != null && reference.getId() != null) {
      result = productions.get(reference.getId());
    }

    return result;
  }

  public ProductionPackage fromDTO(ProductionDTO dto) {

    if (dto == null) {
      return null;
    }

    ProductionPackage result = new ProductionPackage();

    Production production = null;
    if (dto.getId() != null) {
      production = productions.get(dto.getId());
    } else {
      production = new Production();
    }

    production.setProductionType(typeConverter.fromDTO(dto.getProductionType(), true));
    production.setTitle(dto.getTitle());
    production.setSubtitle(dto.getSubtitle());
    production.setDescription(dto.getDescription());
    production.setDurationInMinutes(dto.getDurationInMinutes());
    production.setRating(dto.getRating());
    production.setRecommendedAge(dto.getRecommendedAge());

    result.setProduction(production);

    if (dto.getMetadata() != null) {
      result.setMetaValueList(dto.getMetadata().stream()
              .map(value -> metaValueConverter.fromDTO(value, result.getProduction(), false))
              .toList());
    }

    if (dto.getVersions() != null) {
      result.setVersionList(dto.getVersions().stream()
              .map(version -> versionConverter.fromDTO(version, result.getProduction(), false))
              .toList());
    }

    if (dto.getAttachments() != null) {
      result.setAttachmentList(dto.getAttachments().stream()
              .map(a -> attachmentConverter.fromDTO(a))
              .toList());
    }

    if (dto.getLicenseConditions() != null) {
      result.setLicenseList(dto.getLicenseConditions().stream()
              .map(l -> licenseConverter.fromDTO(l))
              .toList());
    }

    return result;
  }

  public static class ProductionPackage {

    private Production production;
    private List<ProductionVersion> versionList;
    private List<ProductionMetaValue> metaValueList;
    private List<Attachment> attachmentList;
    private List<LicenseCondition> licenseList;

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
     * @return the metaValueList
     */
    public List<ProductionMetaValue> getMetaValueList() {
      return metaValueList;
    }

    /**
     * @param metaValueList the metaValueList to set
     */
    public void setMetaValueList(List<ProductionMetaValue> metaValueList) {
      this.metaValueList = metaValueList;
    }

    /**
     * @return the attachmentList
     */
    public List<Attachment> getAttachmentList() {
      return attachmentList;
    }

    /**
     * @param attachmentList the attachmentList to set
     */
    public void setAttachmentList(List<Attachment> attachmentList) {
      this.attachmentList = attachmentList;
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
    //</editor-fold>

  }
}
