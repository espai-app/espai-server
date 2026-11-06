
package app.espai.dto;

import app.espai.dao.Agencies;
import app.espai.model.Agency;
import app.espai.sdk.model.AgencyDTO;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class AgencyDTOConverter {

  @EJB
  private Agencies agencies;

  public AgencyDTO toDTO(Agency agency) {
    if (agency == null) {
      return null;
    }

    AgencyDTO result = new AgencyDTO();
    result.setId(agency.getId());
    result.setName(agency.getName());

    return result;
  }

  public Agency fromDTO(AgencyDTO agency) {
    if (agency == null) {
      return null;
    }

    Agency result = null;
    if (agency.getId() != null) {
      result = agencies.get(agency.getId());
    }

    if (result == null) {
      result = agencies.getByName(agency.getName());
    }

    if (result == null) {
      result = new Agency();
    }

    result.setName(agency.getName());

    return result;
  }
}
