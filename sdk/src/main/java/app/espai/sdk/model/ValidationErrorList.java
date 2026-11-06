package app.espai.sdk.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rborowski
 */
public class ValidationErrorList {

  private List<ValidationError> errors;

  public ValidationErrorList() {
    errors = new LinkedList<>();
  }

  public ValidationErrorList(List<ValidationError> errors) {
    this.errors = errors;
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the errors
   */
  public List<ValidationError> getErrors() {
    return errors;
  }

  /**
   * @param errors the errors to set
   */
  public void setErrors(List<ValidationError> errors) {
    this.errors = errors;
  }
  //</editor-fold>

}
