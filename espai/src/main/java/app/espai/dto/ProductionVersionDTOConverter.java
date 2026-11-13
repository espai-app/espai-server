
package app.espai.dto;

import app.espai.dao.ProductionVersions;
import app.espai.model.Production;
import app.espai.model.ProductionVersion;
import app.espai.sdk.model.ProductionVersionDTO;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class ProductionVersionDTOConverter {

  @EJB
  private ProductionVersions versions;

  public ProductionVersionDTO toDTO(ProductionVersion version) {

    if (version == null) {
      return null;
    }

    ProductionVersionDTO result = new ProductionVersionDTO();
    result.setId(version.getId());
    result.setVersionName(version.getVersionName());
    result.setMediaFormat(version.getMediaFormat());
    result.setSpokenLanguage(version.getSpokenLanguage());
    result.setAudioDescription(version.isAudioDescription());
    result.setSubtitles(version.getSubtitles());
    result.setClosedCaption(version.isClosedCaption());

    return result;
  }

  public ProductionVersion fromDTO(ProductionVersionDTO version, Production production, boolean readonly) {

    if (version == null) {
      return null;
    }

    ProductionVersion result = null;
    if (version.getId() != null) {
      result = versions.get(version.getId());
    }

    if (readonly) {
      return result;
    }

    if (result == null) {
      result = new ProductionVersion();
    }

    result.setProduction(production);
    result.setVersionName(version.getVersionName());
    result.setMediaFormat(version.getMediaFormat());
    result.setSpokenLanguage(version.getSpokenLanguage());
    result.setAudioDescription(version.isAudioDescription());
    result.setSubtitles(version.getSubtitles());
    result.setClosedCaption(version.isClosedCaption());

    return result;
  }
}
