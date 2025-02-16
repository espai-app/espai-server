/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.sdk.model;

/**
 *
 * @author rborowski
 */
public class MovieDTO extends ProductionDTO {

  private String productionCountries;
  private String productionYear;
  private String director;
  private String starring;
  private String book;

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the productionCountries
   */
  public String getProductionCountries() {
    return productionCountries;
  }

  /**
   * @param productionCountries the productionCountries to set
   */
  public void setProductionCountries(String productionCountries) {
    this.productionCountries = productionCountries;
  }

  /**
   * @return the productionYear
   */
  public String getProductionYear() {
    return productionYear;
  }

  /**
   * @param productionYear the productionYear to set
   */
  public void setProductionYear(String productionYear) {
    this.productionYear = productionYear;
  }

  /**
   * @return the director
   */
  public String getDirector() {
    return director;
  }

  /**
   * @param director the director to set
   */
  public void setDirector(String director) {
    this.director = director;
  }

  /**
   * @return the starring
   */
  public String getStarring() {
    return starring;
  }

  /**
   * @param starring the starring to set
   */
  public void setStarring(String starring) {
    this.starring = starring;
  }

  /**
   * @return the book
   */
  public String getBook() {
    return book;
  }

  /**
   * @param book the book to set
   */
  public void setBook(String book) {
    this.book = book;
  }
  //</editor-fold>

}
