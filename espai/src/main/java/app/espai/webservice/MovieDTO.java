/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.webservice;

import app.espai.model.Attachment;
import app.espai.model.Movie;
import app.espai.model.ProductionTag;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

  public static MovieDTO of(Movie production, List<Attachment> attachments) {
    MovieDTO result = new MovieDTO();
    result.setId(production.getId());
    result.setTitle(production.getTitle());
    result.setDescription(production.getDescription());
    result.setDurationInMinutes(production.getDurationInMinutes());
    result.setFromAge(production.getFromAge());
    result.setToAge(production.getToAge());
    result.setRating(production.getRating());
    result.setProductionCountries(production.getProductionCountries());
    result.setProductionYear(production.getProductionYear());
    result.setDirector(production.getDirector());
    result.setStarring(production.getStarring());
    result.setBook(production.getBook());

    if (attachments != null) {
      Map<String, List<AttachmentDTO>> attachmentDTOs = new LinkedHashMap<>();
      for (Attachment a : attachments) {
        if (!attachmentDTOs.containsKey(a.getCategory())) {
          attachmentDTOs.put(a.getCategory(), new LinkedList<>());
        }
        attachmentDTOs.get(a.getCategory()).add(AttachmentDTO.of(a));
      }
      result.setAttachments(attachmentDTOs);
    }

    return result;
  }

  public static MovieDTO of(Movie production,
          List<Attachment> attachments,
          List<ProductionTag> productionTags) {

    MovieDTO result = MovieDTO.of(production, attachments);

    Map<String, List<String>> tags = new LinkedHashMap<>();
    for (ProductionTag t : productionTags) {
      if (!tags.containsKey(t.getCategory())) {
        tags.put(t.getCategory(), new LinkedList<>());
      }
      tags.get(t.getCategory()).add(t.getValue());
    }
    result.setTags(tags);

    return result;
  }

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
