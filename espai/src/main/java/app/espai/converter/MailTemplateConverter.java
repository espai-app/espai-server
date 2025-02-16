package app.espai.converter;

import app.espai.dao.MailTemplates;
import app.espai.model.MailTemplate;
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
@FacesConverter(forClass = MailTemplate.class, managed = true)
public class MailTemplateConverter implements Converter {

  @Inject
  private MailTemplates mailTemplates;

  @Override
  public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
    try {
      if (string == null || !string.matches("\\d+")) {
        return null;
      }

      return mailTemplates.get(Long.parseLong(string));
    } catch (ResourceNotFoundException ex) {
      return null;
    }
  }

  @Override
  public String getAsString(FacesContext fc, UIComponent uic, Object t) {
    if (t == null) {
      return null;
    }
    return String.valueOf(((MailTemplate) t).getId());
  }

}
