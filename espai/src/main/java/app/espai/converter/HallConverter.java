package app.espai.converter;

import app.espai.dao.Halls;
import app.espai.model.Hall;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 *
 * @author rborowski
 */
@Named
@ApplicationScoped
@FacesConverter(forClass = Hall.class)
public class HallConverter implements Converter {

  @EJB
  private Halls halls;

  @Override
  public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
    try {
      if (string == null || !string.matches("\\d+")) {
        return null;
      }
      
      return halls.get(Long.parseLong(string));
    } catch (ResourceNotFoundException ex) {
      return null;
    }
  }

  @Override
  public String getAsString(FacesContext fc, UIComponent uic, Object t) {
    if (t == null) {
      return null;
    }
    return String.valueOf(((Hall) t).getId());
  }

}
