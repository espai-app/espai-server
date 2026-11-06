
package app.espai;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.util.ResourceBundle;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class Translation {

  private FacesContext context = FacesContext.getCurrentInstance();
  private ResourceBundle resourceBundle = ResourceBundle.getBundle(
          "app.espai.messages", context.getViewRoot().getLocale());

  public String get(String key) {
    return resourceBundle.getString(key);
  }

}
