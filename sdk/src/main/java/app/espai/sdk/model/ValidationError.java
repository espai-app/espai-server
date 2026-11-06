
package app.espai.sdk.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author rborowski
 */
@Schema(name = "ValidationError",
        description = "Represents a validation error send from the server.")
public class ValidationError {

  private String path;
  private String errorMessage;

  public ValidationError() {

  }

  public ValidationError(String path, String errorMessage) {
    this.path = path;
    this.errorMessage = errorMessage;
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return the errorMessage
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * @param errorMessage the errorMessage to set
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  //</editor-fold>

}
