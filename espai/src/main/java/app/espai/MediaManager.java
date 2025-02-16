package app.espai;

import app.espai.model.Attachment;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 *
 * @author rborowski
 */
@Named
@Stateless
public class MediaManager {

  private static final String DIGEST_ALGORITHM = "SHA1";

  private File dataFolder;

  @PostConstruct
  public void init() {
    dataFolder = new File(ConfigProvider.getConfig().getValue("data.folder", String.class));

    if (!dataFolder.exists() && !dataFolder.mkdirs()) {
      throw new RuntimeException("Could not access data folder.");
    }
  }

  public InputStream get(Attachment attachment) throws IOException {

    File source = attachmentToFile(attachment);
    if (source == null || !source.exists()) {
      throw new FileNotFoundException("Attachment not found.");
    }

    return new FileInputStream(source);
  }

  public InputStream getScaled(Attachment attachment, int width, int height) throws IOException {
    File source = attachmentToFile(attachment);

    Derivative scaled = new Derivative(source, width, height, Derivative.ScaleType.FIT_BOX);
    File target = scaled.toFile();

    if (!scaled.toFile().exists()) {
      ImageConverterImageMagick.resize(source, target, width, height);
    }

    return new FileInputStream(target);
  }

  public InputStream getCropped(Attachment attachment, int width, int height) throws IOException {
    File source = attachmentToFile(attachment);

    Derivative scaled = new Derivative(source, width, height, Derivative.ScaleType.CROPPED);
    File target = scaled.toFile();

    if (!scaled.toFile().exists()) {
      ImageConverterImageMagick.resize(source, target, width, height);
    }

    return new FileInputStream(target);
  }

  public List<Derivative> listDerivatives(Attachment attachment) throws FileNotFoundException {
    List<Derivative> result = new LinkedList<>();
    File attachmentFile = attachmentToFile(attachment);
    if (attachmentFile == null || !attachmentFile.exists()) {
      throw new FileNotFoundException("Attachment not found.");
    }

    String derivativePattern = attachmentFile.getName() + "__";

    for (File f : attachmentFile.getParentFile().listFiles()) {
      if (!f.isFile() || !f.getName().startsWith(derivativePattern)) {
        continue;
      }
      result.add(new Derivative(f));
    }

    return result;
  }

  public void save(Attachment attachment, InputStream inputStream) throws IOException {
    File targetFile = attachmentToFile(attachment);
    File parentFolder = targetFile.getParentFile();
    if (!parentFolder.exists() && !parentFolder.mkdirs()) {
      throw new IOException(String.format("Could not create target folder %s",
              parentFolder.getAbsolutePath()));
    }

    try {
      MessageDigest md = MessageDigest.getInstance("SHA1");

      try (DigestInputStream dis = new DigestInputStream(inputStream, md);
              FileOutputStream outputStream = new FileOutputStream(targetFile)) {

        dis.transferTo(outputStream);
      }

      String fx = "%0" + (md.getDigestLength()*2) + "x";
      attachment.setChecksum(String.format(fx, new BigInteger(1, md.digest())));
    } catch (NoSuchAlgorithmException ex) {
      throw new IOException("Could not create checksum for upload.", ex);
    }
  }

  public void delete(Attachment attachment) {
    try {
      List<Derivative> derivatives = listDerivatives(attachment);
      for (Derivative d : derivatives) {
        d.toFile().delete();
      }
    } catch (FileNotFoundException ex) {
      // ignore
    }

    File attachmentFile = attachmentToFile(attachment);
    if (attachmentFile.exists()) {
      attachmentFile.delete();
    }
  }

  private File attachmentToFile(Attachment attachment) {
    if (attachment.getId() == null || attachment.getEntityType() == null
            || attachment.getEntityId() == null) {
      return null;
    }

    String path = attachment.getEntityType() + File.separator
            + attachment.getEntityId() + File.separator
            + attachment.getId();

    File result = new File(dataFolder, path);
    try {
      if (!result.getCanonicalPath().startsWith(dataFolder.getCanonicalPath())) {
        return null;
      }
    } catch (IOException ex) {
      Logger.getLogger(MediaManager.class.getName()).log(
              Level.WARNING,
              String.format("Could not convert attachment to file (%s)", path),
              ex);

      return null;
    }

    return result;
  }

  public String createSha1Checksum(File file) throws IOException {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA1");
      md.digest(Files.readAllBytes(file.toPath()));
      String fx = "%0" + (md.getDigestLength()*2) + "x";
      return String.format(fx, new BigInteger(1, md.digest()));
    } catch (NoSuchAlgorithmException ex) {
      throw new IOException("Could not create checksum for upload.", ex);
    }
  }

  public class Derivative {

    public enum ScaleType {
      CROPPED, FIT_BOX
    }

    private final String name;
    private final String path;
    private final int width;
    private final int height;
    private final ScaleType scaleType;

    public Derivative(File attachmentFile, int width, int height, ScaleType type) {
      this.name = attachmentFile.getName();
      this.width = width;
      this.height = height;
      this.scaleType = type;
      this.path = new File(attachmentFile.getParent(), toFilename()).getPath();
    }

    public Derivative(File file) {
      this.path = file.getPath();

      String[] parts = file.getName().split("_");
      this.name = parts[0];
      if (parts[2].equals("c")) {
        this.scaleType = ScaleType.CROPPED;
      } else {
        this.scaleType = ScaleType.FIT_BOX;
      }

      this.height = Integer.parseInt(parts[3]);
      this.width = Integer.parseInt(parts[4]);
    }

    private String toFilename() {
      return this.name + "__" + (this.scaleType == ScaleType.CROPPED ? 'c' : 'f')
              + "_" + this.width + "_" + this.height;
    }

    public File toFile() {
      return new File(this.path);
    }
  }
}
