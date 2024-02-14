package app.espai;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

/**
 * Interface for ImageMagick to crop, resize and rotate images.
 *
 * @author Ralph Borowski
 */
public class ImageConverterImageMagick {

  /**
   * Resizes and crops an image to the given dimensions.
   *
   * @param source the source image
   * @param target the resized and croped image
   * @param targetWidth width of the final image
   * @param targetHeight height of the final image
   * @throws IOException if image could not be manipulated
   */
  public static void resizeAndCrop(File source, File target, int targetWidth, int targetHeight) throws IOException {

    try {
      // create command
      ConvertCmd cmd = new ConvertCmd();

      // create the operation
      IMOperation op = new IMOperation();

      // set input file
      op.addImage(source.getAbsolutePath());

      // get original dimensions
      BufferedImage originalImage = ImageIO.read(source);
      int imageHeight = originalImage.getHeight();
      int imageWidth = originalImage.getWidth();

      // free resources
      originalImage = null;

      // calculate new size
      float heightRatio = (float) targetHeight / imageHeight;
      float widthRatio = (float) targetWidth / imageWidth;

      int tempHeight;
      int tempWidth;

      if (heightRatio > widthRatio) {
        tempHeight = Math.round(imageHeight * heightRatio);
        tempWidth = Math.round(imageWidth * heightRatio);
      } else {
        tempHeight = Math.round(imageHeight * widthRatio);
        tempWidth = Math.round(imageWidth * widthRatio);
      }

      if (tempHeight % 2 == 1) {
        tempHeight++;
      }
      if (tempWidth % 2 == 1) {
        tempWidth++;
      }

      // resize image
      op.resize(tempWidth, tempHeight);

      // center image
      op.gravity("center");

      // crop to target size
      op.crop(targetWidth, targetHeight, 0, 0);

      // rewrite headers
      op.p_repage();

      // set target file
      op.addImage(target.getAbsolutePath());

      // execute the operation
      cmd.run(op);

    } catch (IM4JavaException | InterruptedException ex) {
      throw new IOException("Error in im4Java occured.", ex);
    }
  }

  /**
   * Resizes the image to fit into the given dimensions.
   *
   * @param source the source image
   * @param target the resized image
   * @param targetWidth width of the targed image
   * @param targetHeight height of the target image
   * @throws IOException if the image could not be manipulated
   */
  public static void resize(File source, File target, int targetWidth, int targetHeight) throws IOException {

    try {
      // create command
      ConvertCmd cmd = new ConvertCmd();

      // create the operation
      IMOperation op = new IMOperation();

      // add source file
      op.addImage(source.getAbsolutePath());

      // resize
      op.resize(targetWidth, targetHeight);

      // set target file
      op.addImage(target.getAbsolutePath());

      // execute the operation
      cmd.run(op);
    } catch (IM4JavaException | InterruptedException ex) {
      throw new IOException("Error in im4Java occured.", ex);
    }
  }

  /**
   * Rotated the image according to the EXIF data.
   *
   * @param source source image
   * @param target target image
   * @throws IOException if image could not be rotated
   */
  public static void autoOrient(File source, File target) throws IOException {
    try {
      // create command
      ConvertCmd cmd = new ConvertCmd();

      // create the operation
      IMOperation op = new IMOperation();

      // add source file
      op.addImage(source.getAbsolutePath());

      // resize
      op.autoOrient();

      // set target file
      op.addImage(target.getAbsolutePath());

      // execute the operation
      cmd.run(op);
    } catch (IM4JavaException | InterruptedException ex) {
      throw new IOException("Error in im4Java occured.", ex);
    }
  }

  /**
   * Rotate an image by a given degree.
   *
   * @param source source image
   * @param target rotated image
   * @param degree degreee for rotating clockwise
   * @throws IOException if image could not be manipulated
   */
  public static void rotate(File source, File target, int degree) throws IOException {
    try {
      // create command
      ConvertCmd cmd = new ConvertCmd();

      // create the operation
      IMOperation op = new IMOperation();

      // add source file
      op.addImage(source.getAbsolutePath());

      // resize
      op.rotate( (double)degree );

      // set target file
      op.addImage(target.getAbsolutePath());

      // execute the operation
      cmd.run(op);
    } catch (IM4JavaException | InterruptedException ex) {
      throw new IOException("Error in im4Java occured.", ex);
    }
  }
}