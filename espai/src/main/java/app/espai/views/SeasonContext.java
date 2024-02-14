package app.espai.views;

import app.espai.Constants;
import app.espai.dao.Seasons;
import app.espai.model.Season;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 *
 * @author rborowski
 */
@Named
@Stateless
public class SeasonContext {

  @EJB
  private Seasons seasons;

  public Season getCurrentSeason() {
    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();
    String seasonId = econtext.getRequestParameterMap().get(Constants.SEASON_ID);

    if (seasonId != null && seasonId.matches("\\d+")) {
      return seasons.get(Long.parseLong(seasonId));
    }

    return null;
  }
}
