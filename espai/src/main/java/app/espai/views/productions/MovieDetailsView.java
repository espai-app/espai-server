package app.espai.views.productions;

import app.espai.dao.Attachments;
import app.espai.dao.LicenseConditions;
import app.espai.dao.Movies;
import app.espai.filter.AttachmentFilter;
import app.espai.model.Attachment;
import app.espai.model.Movie;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class MovieDetailsView {

  @EJB
  private Movies movies;

  @EJB
  private Attachments attachments;

  @EJB
  private LicenseConditions conditions;

  private Movie movie;
  private Attachment poster;

  @PostConstruct
  public void init() {

    ExternalContext econtext = FacesContext.getCurrentInstance().getExternalContext();

    String movieIdParam = econtext.getRequestParameterMap().get("movieId");
    if (movieIdParam == null || !movieIdParam.matches("\\d+")) {
      return;
    }

    movie = movies.get(Long.parseLong(movieIdParam));

    AttachmentFilter attachmentFilter = new AttachmentFilter();
    attachmentFilter.setEntityType(Movie.class.getSimpleName());
    attachmentFilter.setEntityId(movie.getId());
    attachmentFilter.setCategory("Poster");

    List<Attachment> posters = attachments.list(attachmentFilter).getItems();
    if (!posters.isEmpty()) {
      poster = posters.get(0);
    }
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the movie
   */
  public Movie getMovie() {
    return movie;
  }

  /**
   * @param movie the movie to set
   */
  public void setMovie(Movie movie) {
    this.movie = movie;
  }

  /**
   * @return the poster
   */
  public Attachment getPoster() {
    return poster;
  }

  /**
   * @param poster the poster to set
   */
  public void setPoster(Attachment poster) {
    this.poster = poster;
  }
  //</editor-fold>

}
