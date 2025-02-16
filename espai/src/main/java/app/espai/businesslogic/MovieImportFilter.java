package app.espai.businesslogic;

import app.espai.webservice.AttachmentImport;
import app.espai.webservice.MovieImport;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author rborowski
 */
public interface MovieImportFilter {

  public void setConnection(String url, String username, String password);

  public List<MovieImport> getMovies() throws IOException;
  public InputStream getAttachment(AttachmentImport attachment) throws IOException;

}
