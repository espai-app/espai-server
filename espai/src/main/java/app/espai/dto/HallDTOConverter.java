
package app.espai.dto;

import app.espai.dao.HallCapacities;
import app.espai.filter.HallCapacityFilter;
import app.espai.model.Hall;
import app.espai.sdk.model.HallCapacityDTO;
import app.espai.sdk.model.HallDTO;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class HallDTOConverter {

  @EJB
  private HallCapacities capacities;

  @Inject
  private VenueDTOConverter venueDTOConverter;

  @Inject
  private HallCapacityDTOConverter capacityConverter;

  public HallDTO toDTO(Hall hall) {

    HallCapacityFilter capacityFilter = new HallCapacityFilter();
    capacityFilter.setHall(hall);
    List<HallCapacityDTO> hallCapacityList = capacities.list(capacityFilter).getItems()
            .stream()
            .map(h -> capacityConverter.toDTO(h))
            .toList();

    HallDTO result = new HallDTO();
    result.setId(hall.getId());
    result.setName(hall.getName());
    result.setVenue(venueDTOConverter.toDTO(hall.getVenue()));
    result.setHallCapacities(hallCapacityList);

    return result;
  }

}
