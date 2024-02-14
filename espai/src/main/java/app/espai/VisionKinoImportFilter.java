package app.espai;

import app.espai.model.Agency;
import app.espai.model.Attachment;
import app.espai.model.AttachmentType;
import app.espai.model.Movie;
import app.espai.model.ProductionTag;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author rborowski
 */
public class VisionKinoImportFilter {

  private static DateTimeFormatter GERMAN_DATES = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  public List<String> getMovies() throws IOException {

    String[] movieIndexes = new String[]{
      "https://www.visionkino.de/schulkinowochen/filmangebot/"
      // "https://www.visionkino.de/filmtipps/"
    };

    List<String> movieList = new LinkedList<>();

    for (String indexPage : movieIndexes) {

      // parse first page
      System.out.println("Getting " + indexPage);
      Document indexDocument = Jsoup.connect(indexPage).get();
      movieList.addAll(parseMovieIndexPage(indexDocument));

      // parse
      Elements pageLinks = indexDocument.select(".pagination a");
      for (Element link : pageLinks) {
        String href = link.absUrl("href");
        if (!href.isBlank()) {
          System.out.println("Getting " + href);
          Document doc = Jsoup.connect(href).get();
          movieList.addAll(parseMovieIndexPage(doc));
        }

        try {
          Thread.sleep(2000);
        } catch (InterruptedException ex) {
          Logger.getLogger(VisionKinoImportFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return movieList;
  }

  public List<String> parseMovieIndexPage(Document page) throws IOException {
    List<String> result = new LinkedList<>();

    Elements movieLinks = page.select(".moviedb-listing-single-element a");

    for (Element l : movieLinks) {
      String href = l.absUrl("href");
      if (!result.contains(href)) {
        result.add(href);
      }
    }

    return result;
  }
//      Element imgTag = l.selectFirst("img");
//      ProductionAttachment still = null;
//      if (imgTag != null) {
//        still = new ProductionAttachment();
//        still.setMediaType(AttachmentType.IMAGE);
//        still.setCategory("Szenenbild");
//
//        String title = imgTag.attr("title");
//        if (title != null && title.contains(",")) {
//          int titleSepatator = title.lastIndexOf(",");
//          still.setCaption(title.substring(0, titleSepatator).trim());
//          still.setCopyright(title.substring(titleSepatator + 1).trim());
//        }
//
//        still.setLocation(imgTag.absUrl("src"));
//      }

  public MovieWithRelations parseMovieDetailsPage(String movieUrl)
          throws IOException {

    Document movieDetails = Jsoup.connect(movieUrl).get();

    // parse movie
    Movie movie = new Movie();
    List movieAttachments = new LinkedList<>();
    List<ProductionTag> tags = new LinkedList<>();
    MovieWithRelations movieWithRelations
            = new MovieWithRelations(movie, movieAttachments, tags);

    movie.setTitle(movieDetails.selectFirst("h1").text());

    Element description = movieDetails.selectFirst("p.inhalt");
    if (description != null) {
      movie.setDescription(description.text());
    }

    Element productionInfo = movieDetails.selectFirst(".movie-single-desc-subtitle");
    if (productionInfo != null) {
      String countryAndDate = productionInfo.text();
      int lastSpace = countryAndDate.lastIndexOf(" ");
      movie.setProductionCountries(countryAndDate.substring(0, lastSpace).trim());
      movie.setProductionYear(countryAndDate.substring(lastSpace).trim());
    }

    Element movieInfos = movieDetails.selectFirst(".movie-single-main-info");
    if (movieInfos != null) {
      parseInfo(movieWithRelations, movieInfos.children());
    }

    Element movieTags = movieDetails.selectFirst(".movie-single-infos");
    if (movieTags != null) {
      parseInfo(movieWithRelations, movieTags.children());
    }

    Elements downloads = movieDetails.select(".movie-single-infos-downloads a");
    for (Element d : downloads) {
      Attachment a = new Attachment();
      a.setMediaType(AttachmentType.URL);
      a.setCategory("Downloads");
      a.setCaption(d.text());
      a.setLocation(d.absUrl("href"));
      movieAttachments.add(a);
    }

    Elements externalLinks = movieDetails.select(".movie-single-infos-links a");
    for (Element d : externalLinks) {
      Attachment a = new Attachment();
      a.setMediaType(AttachmentType.URL);
      a.setCategory("Externe Links");
      a.setCaption(d.text());
      a.setLocation(d.absUrl("href"));
      movieAttachments.add(a);
    }

    Element poster = movieDetails.selectFirst(".movie-single-image img");
    if (poster != null) {
      Attachment p = new Attachment();
      p.setMediaType(AttachmentType.IMAGE);
      p.setCategory("Poster");
      p.setLocation(poster.absUrl("src"));
      p.setCaption(poster.attr("alt"));

      String imageTitle = poster.attr("title");
      p.setCopyright(imageTitle.substring(imageTitle.lastIndexOf(",") + 1).trim());

      movieAttachments.add(p);
    }

    Element trailer = movieDetails.selectFirst(".movie-single-trailer iframe");
    if (trailer != null) {
      Attachment t = new Attachment();
      t.setMediaType(AttachmentType.VIDEO);
      t.setCategory("Trailer");
      t.setLocation(trailer.attr("src"));
      t.setCaption("Trailer");

      movieAttachments.add(t);
    }

    return movieWithRelations;
  }

  private MovieWithRelations parseInfo(MovieWithRelations movie, Elements movieInfos) {
    String heading = "";
    for (Element e : movieInfos) {
      if (e.tagName().equalsIgnoreCase("div") && !e.hasClass("movie-single-infos-sign")) {
        break;
      }

      if (e.hasClass("movie-single-infos-sign")) {
        heading = e.text();
      }

      if (e.nodeName().equalsIgnoreCase("p")) {
        switch (heading) {
          case "Genre":
          case "Unterrichtsfächer":
          case "Themen":
          case "Altersempfehlung":
          case "Festivals":
          case "FBW":
            String[] movieTags = e.text().split(",");
            for (String t : movieTags) {
              t = t.trim();
              if (!t.isBlank()) {
                movie.getTags().add(new ProductionTag(null, heading, t.trim()));
              }
            }
            break;
          case "Kinostart":
            movie.getMovie().setReleaseDate(LocalDate.parse(e.text(), GERMAN_DATES));
            break;
          case "Regie":
            movie.getMovie().setDirector(e.text());
            break;
          case "Buch":
            movie.getMovie().setBook(e.text());
            break;
          case "Darsteller*innen":
            movie.getMovie().setStarring(e.text());
            break;
          case "Länge":
            movie.getMovie().setDurationInMinutes(Integer.parseInt(e.text().split(" ")[0]));
            break;
          case "Sprachfassung":
            // Todo
            break;
          case "FSK":
            movie.getMovie().setRating(switch (e.text()) {
              case "ohne Altersbeschränkung" ->
                0;
              case "ab 6 Jahre" ->
                6;
              case "ab 12 Jahre" ->
                12;
              case "ab 16 Jahre" ->
                16;
              default ->
                -1;
            });
            break;
          case "Verleih":
            Agency agency = new Agency();
            agency.setName(e.text());
            movie.getMovie().setAgency(agency);
            break;
        }
      }
    }

    return movie;
  }

  public static class MovieWithRelations {

    private Movie movie;
    private List<Attachment> attachments;
    private List<ProductionTag> tags;

    public MovieWithRelations() {

    }

    public MovieWithRelations(
            Movie movie,
            List<Attachment> attachments,
            List<ProductionTag> tags) {
      this.movie = movie;
      this.attachments = attachments;
      this.tags = tags;
    }

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
     * @return the attachments
     */
    public List<Attachment> getAttachments() {
      return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(List<Attachment> attachments) {
      this.attachments = attachments;
    }

    /**
     * @return the tags
     */
    public List<ProductionTag> getTags() {
      return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(List<ProductionTag> tags) {
      this.tags = tags;
    }

  }
}
