package app.espai.converter;

import app.espai.dao.SeasonProductions;
import app.espai.model.Season;
import app.espai.model.SeasonProduction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 *
 * @author rborowski
 */
@Named
@ApplicationScoped
@FacesConverter(forClass = SeasonProduction.class)
public class SeasonProductionConverter implements Converter {

  @Inject
  private SeasonProductions productions;

  @Override
  public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
    try {
      if (string == null || !string.matches("\\d+")) {
        return null;
      }
      
      return productions.get(Long.parseLong(string));
    } catch (ResourceNotFoundException ex) {
      return null;
    }
  }

  @Override
  public String getAsString(FacesContext fc, UIComponent uic, Object t) {
    if (t == null) {
      return null;
    }
    return String.valueOf(((Season) t).getId());
  }

}
