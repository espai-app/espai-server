/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.MediaManager;
import app.espai.model.Attachment;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.InputStream;

/**
 * DAO class for attachment
 */
@Named
@Stateless
public class Attachments extends AbstractAttachments {

  @EJB
  private MediaManager mediaManager;

  public void setDataStream(Attachment attachment, InputStream inputStream) throws IOException {
    mediaManager.save(attachment, inputStream);
  }

  public InputStream getDataStream(Attachment attachment) throws IOException {
    return mediaManager.get(attachment);
  }

  public InputStream getScaledImageStream(Attachment attachment, int width, int height)
          throws IOException {

    return mediaManager.getScaled(attachment, width, height);
  }

  public InputStream getCroppedImageStream(Attachment attachment, int width, int height)
          throws IOException {

    return mediaManager.getCropped(attachment, width, height);
  }

  public void delete(Attachment attachment) {
    mediaManager.delete(attachment);
    super.delete(attachment);
  }

}