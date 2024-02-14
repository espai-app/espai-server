package app.espai.webservice;

import app.espai.model.Presenter;

/**
 *
 * @author rborowski
 */
public class PresenterDTO {

  private Long id;
  private String givenName;
  private String surname;

  public static PresenterDTO of(Presenter presenter) {
    PresenterDTO result = new PresenterDTO();
    result.setId(presenter.getId());
    result.setGivenName(presenter.getGivenName());
    result.setSurname(presenter.getSurname());

    return result;
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the givenName
   */
  public String getGivenName() {
    return givenName;
  }

  /**
   * @param givenName the givenName to set
   */
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  /**
   * @return the surname
   */
  public String getSurname() {
    return surname;
  }

  /**
   * @param surname the surname to set
   */
  public void setSurname(String surname) {
    this.surname = surname;
  }
  //</editor-fold>
}
