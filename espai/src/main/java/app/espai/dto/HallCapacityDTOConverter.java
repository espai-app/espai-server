
package app.espai.dto;

import app.espai.model.HallCapacity;
import app.espai.sdk.model.HallCapacityDTO;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class HallCapacityDTOConverter {

  public HallCapacityDTO toDTO(HallCapacity hallCapacity) {

    HallCapacityDTO result = new HallCapacityDTO();
    result.setSeatCategoryId(hallCapacity.getSeatCategory().getId());
    result.setSeatCategoryName(hallCapacity.getSeatCategory().getName());
    result.setCapacity(hallCapacity.getCapacity());

    return result;
  }

}
