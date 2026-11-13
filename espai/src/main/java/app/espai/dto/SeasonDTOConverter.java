
package app.espai.dto;

import app.espai.dao.Seasons;
import app.espai.model.Season;
import app.espai.sdk.model.SeasonDTO;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class SeasonDTOConverter {

  @EJB
  private Seasons seasons;

  public SeasonDTO toDTO(Season season) {
    SeasonDTO result = new SeasonDTO();
    result.setName(season.getName());
    result.setStart(season.getStart());
    result.setEnd(season.getEnd());

    return result;
  }

  public Season fromDTO(SeasonDTO dto) {

    if (dto == null) {
      return null;
    }

    if (dto.getId() != null) {
      return seasons.get(dto.getId());
    }

    return null;
  }

}
