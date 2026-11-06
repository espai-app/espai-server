
package app.espai.dto;

import app.espai.dao.LicenseConditions;
import app.espai.model.LicenseCondition;
import app.espai.sdk.model.LicenseConditionDTO;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import rocks.xprs.types.Monetary;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class LicenseConditionDTOConverter {

  @EJB
  private LicenseConditions licenses;

  @Inject
  private AgencyDTOConverter agencyConverter;

  public LicenseConditionDTO toDTO(LicenseCondition license) {
    if (license == null) {
      return null;
    }

    LicenseConditionDTO result = new LicenseConditionDTO();
    result.setId(license.getId());
    result.setAgency(agencyConverter.toDTO(license.getAgency()));

    if (license.getAdditionalCost() != null) {
      result.setAdditionalCost(license.getAdditionalCost().getAmount());
      result.setCurrency(license.getAdditionalCost().getCurrency());
    }

    if (license.getFixedPrice() != null) {
      result.setAdditionalCost(license.getFixedPrice().getAmount());
      result.setCurrency(license.getFixedPrice().getCurrency());
    }

    if (license.getGuarantee() != null) {
      result.setAdditionalCost(license.getGuarantee().getAmount());
      result.setCurrency(license.getGuarantee().getCurrency());
    }

    if (license.getTicketShare() != null) {
      result.setTicketShare(license.getTicketShare());
    }

    return result;
  }

  public LicenseCondition fromDTO(LicenseConditionDTO license) {

    if (license == null) {
      return null;
    }

    LicenseCondition result = null;
    if (license.getId() != null) {
      result = licenses.get(license.getId());
    }

    if (result == null) {
      result = new LicenseCondition();
    }

    result.setAgency(agencyConverter.fromDTO(license.getAgency()));

    if (license.getAdditionalCost() != null) {
      result.setAdditionalCost(new Monetary(license.getAdditionalCost(), license.getCurrency()));
    }

    if (license.getFixedPrice() != null) {
      result.setFixedPrice(new Monetary(license.getFixedPrice(), license.getCurrency()));
    }

    if (license.getGuarantee() != null) {
      result.setGuarantee(new Monetary(license.getGuarantee(), license.getCurrency()));
    }

    if (license.getTicketShare() != null) {
      result.setTicketShare(license.getTicketShare());
    }

    return result;
  }
}
