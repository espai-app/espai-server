package app.espai.businesslogic;

import app.espai.model.AttachmentType;
import app.espai.webservice.AttachmentImport;
import app.espai.webservice.LicenseConditionImport;
import app.espai.webservice.MovieImport;
import app.espai.webservice.ProductionTagImport;
import app.espai.webservice.ProductionVersionImport;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author rborowski
 */
public class VisionKinoMovieImportFilter implements MovieImportFilter {

  private HttpClient httpClient;

  private String url;
  private String username;
  private String password;
  private String authorizationHeader;

  @Override
  public void setConnection(String url, String username, String password) {
    this.url = url;
    this.username = username;
    this.password = password;
    this.authorizationHeader = "Basic " + Base64.getEncoder()
            .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

    httpClient = HttpClient
            .newBuilder()
            .build();
  }

  @Override
  public List<MovieImport> getMovies() throws IOException {

    File movieHtmlFile = File.createTempFile("vikim", ".html");

    try {

      HttpRequest getListRequest = HttpRequest
              .newBuilder(URI.create(url))
              .GET()
              .setHeader("Authorization", authorizationHeader)
              .build();
      httpClient.send(getListRequest, HttpResponse.BodyHandlers.ofFile(movieHtmlFile.toPath()));

      Document movieListDoc = Jsoup.parse(movieHtmlFile);

      // get movie ids
      Map<String, String> movieForeignIdMap = new HashMap<>();
      Element selectFilm = movieListDoc.selectXpath("//select[@name = 'Film']").first();
      if (selectFilm != null) {
        for (Element option : selectFilm.selectXpath("option")) {
          movieForeignIdMap.put(option.text(), option.attr("value"));
        }
      }

      List<String> fields = new LinkedList<>();
      List<MovieImport> result = new LinkedList<>();
      Element movieTable = movieListDoc.getElementById("uebersicht");

      if (movieTable != null) {
        Elements headings = movieTable.selectXpath("./thead/tr/th");
        for (Element th : headings) {
          fields.add(th.text());
        }

        Element movieBody = movieTable.selectXpath("./tbody").first();
        for (Element r : movieBody.children()) {

          Elements cells = r.children();

          MovieImport m = new MovieImport();
          m.setTitle(cells.get(fields.indexOf("Titel")).text());
          m.setExternalId(movieForeignIdMap.get(m.getTitle()));
          m.setDescription(cells.get(fields.indexOf("Text SKW")).text());
          m.setProductionYear(cells.get(fields.indexOf("Jahr")).text());
          m.setProductionCountries(cells.get(fields.indexOf("Land")).text());
          m.setInternalNote(cells.get(fields.indexOf("Notiz intern")).text());

          String duration = cells.get(fields.indexOf("Länge")).text();
          m.setDurationInMinutes(duration.matches("\\d+") ? Integer.parseInt(duration) : null);

          String age = cells.get(fields.indexOf("Altersempfehlung")).text();
          m.setFromAge(age.matches("\\d+") ? Integer.parseInt(age) : null);

          String rating = cells.get(fields.indexOf("FSK")).text();
          m.setRating(rating.matches("\\d+") ? Integer.parseInt(rating) : null);

          m.setAgency(cells.get(fields.indexOf("Verleih")).text());
          m.setDirector(cells.get(fields.indexOf("Regie")).text());

          // get stills
          Elements stillsRows = cells.get(fields.indexOf("weitere Bilder")).select("tr");
          Element imageRow = stillsRows.first();
          Element creditRow = stillsRows.size() > 1 ? stillsRows.get(1) : null;

          if (imageRow != null) {
            for (int i = 0; i < imageRow.childrenSize(); i++) {
              Element image = imageRow.child(i).getElementsByTag("a").first();
              if (image != null) {
                AttachmentImport still = new AttachmentImport();
                still.setType(AttachmentType.IMAGE);
                still.setCategory("Szenenbild");
                still.setReferenceUrl(image.attr("href"));
                if (creditRow != null && creditRow.childrenSize() > i) {
                  String copyright = creditRow.child(i).text();
                  if (copyright.length() > 300) {
                    copyright = copyright.substring(0, 300);
                  }
                  still.setCopyright(copyright);
                }
                m.addAttachment(still);
              }
            }
          }

          Element posterImage = cells.get(fields.indexOf("Plakat")).select("img").first();
          if (posterImage != null) {
            AttachmentImport poster = new AttachmentImport();
            poster.setType(AttachmentType.IMAGE);
            poster.setCategory("Poster");
            poster.setCopyright("© " + cells.get(fields.indexOf("Verleih")).text());
            poster.setReferenceUrl(posterImage.attr("src"));
            m.addAttachment(poster);
          }

          ProductionVersionImport germanVersion = new ProductionVersionImport();
          germanVersion.setVersionName("");
          germanVersion.setSpokenLanguage("deu");
          m.addVersion(germanVersion);

          List<ProductionTagImport> totalTags = new LinkedList<>();
          totalTags.addAll(toTags("Genre", cells.get(fields.indexOf("Genre")).text()));
          totalTags.addAll(toTags("Themen", cells.get(fields.indexOf("Schlagworte")).text()));
          totalTags.addAll(toTags("Fächer", cells.get(fields.indexOf("Fächer")).text()));
          totalTags.addAll(toTags("Klasse", cells.get(fields.indexOf("Klasse")).text()));
          m.setTags(totalTags);

          LicenseConditionImport license = new LicenseConditionImport();
          license.setAgency(cells.get(fields.indexOf("Vertrieb")).text());
          license.setNotes(cells.get(fields.indexOf("Versandinfos")).text());
          license.setCommercial(true);

          String price = cells.get(fields.indexOf("Mindestgarantie")).text().replace("€", "").trim();
          if (!price.isBlank()) {
            license.setGuarantee(new BigDecimal(price));
          }

          m.addLicenseCondition(license);

          result.add(m);
        }

      }

      return result;
    } catch (InterruptedException ex) {
      throw new IOException(ex);
    } finally {
      movieHtmlFile.delete();
    }
  }

  public List<ProductionTagImport> toTags(String category, String tagString) {
    List<ProductionTagImport> result = new LinkedList<>();

    if (tagString == null || tagString.isBlank()) {
      return result;
    }

    String[] tagList = tagString.split(",");
    for (String t : tagList) {
      t = t.trim();

      if (t.isBlank()) {
        continue;
      }

      ProductionTagImport genreTag = new ProductionTagImport();
      genreTag.setCategory(category);
      genreTag.setValue(t);
      result.add(genreTag);
    }
    return result;
  }

  @Override
  public InputStream getAttachment(AttachmentImport attachment) {
    return null;
  }

  public static void main(String[] args) throws IOException {

    System.setProperty("jdk.httpclient.HttpClient.log", "all");
    System.setProperty("jdk.httpclient.auth.retrylimit", "0");

    VisionKinoMovieImportFilter filter = new VisionKinoMovieImportFilter();
    filter.setConnection("https://viki.db-schulkinowochen.de/extern/skw_tabelle.php", "skw_extern", "x§3=2uFKnZ64");
    List<MovieImport> movies = filter.getMovies();

    for (MovieImport m : movies) {
      System.out.printf("%s - %s\n", m.getExternalId(), m.getTitle());
      if (m.getAttachments() != null) {
        for (AttachmentImport a : m.getAttachments()) {
          System.out.printf("     %s - %s\n", a.getReferenceUrl(), a.getCopyright());
        }
      }
    }
  }
}
