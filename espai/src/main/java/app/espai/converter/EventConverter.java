package app.espai.converter;

import app.espai.dao.Events;
import app.espai.model.Event;
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
@FacesConverter(forClass = Event.class, managed = true)
public class EventConverter implements Converter {

  @Inject
  private Events events;

  @Override
  public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
    try {
      if (string == null || !string.matches("\\d+")) {
        return null;
      }
      
      return events.get(Long.parseLong(string));
    } catch (ResourceNotFoundException ex) {
      return null;
    }
  }

  @Override
  public String getAsString(FacesContext fc, UIComponent uic, Object t) {
    if (t == null) {
      return null;
    }
    return String.valueOf(((Event) t).getId());
  }

}
