package app.espai.converter;

import app.espai.model.ReservationStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;

/**
 *
 * @author rborowski
 */
@Named
@ApplicationScoped
@FacesConverter(forClass = ReservationStatus.class, managed = true)
public class ReservationStatusConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
    if (string == null) {
      return null;
    }
    return ReservationStatus.valueOf(string);
  }

  @Override
  public String getAsString(FacesContext fc, UIComponent uic, Object t) {
    if (t == null) {
      return null;
    }
    return t.toString();
  }

}
