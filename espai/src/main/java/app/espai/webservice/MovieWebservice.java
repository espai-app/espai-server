package app.espai.webservice;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Path("/catalogs/movies")
public class MovieWebservice {

  @POST
  @Path("/import")
  public void importMovies(List<MovieImport> movieImportList) {

  }

}
