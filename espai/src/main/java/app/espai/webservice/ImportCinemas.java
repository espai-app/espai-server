package app.espai.webservice;

import app.espai.dao.Halls;
import app.espai.dao.VenueContacts;
import app.espai.dao.Venues;
import app.espai.model.Hall;
import app.espai.model.Venue;
import app.espai.model.VenueContact;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Path;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author rborowski
 */
@Path("cinemas")
public class ImportCinemas {

  @EJB
  private Venues venues;

  @EJB
  private VenueContacts contacts;

  @EJB
  private Halls halls;

  @Path("import")
  public boolean importCinemas() throws IOException {

    File venueFile = new File("/home/rborowski/Desktop/schulkino/Cinemas.csv");
    File hallFile = new File("/home/rborowski/Desktop/schulkino/Halls.csv");

    Map<String, Venue> venueMap = new HashMap<>();

    try (FileReader fileReader = new FileReader(venueFile);
            CsvMapReader csvReader = new CsvMapReader(
                    fileReader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)) {

      String[] header = csvReader.getHeader(true);
      Map<String, String> line;
      while ((line = csvReader.read(header)) != null) {

        Venue v = new Venue();
        v.setName(line.get("NAME"));
        v.setAddress(line.get("ADDRESS"));
        v.setPostcode(line.get("POSTCODE"));
        v.setCity(line.get("CITY"));
        v.setPhone(line.get("PHONE"));
        v.setEmail(line.get("EMAIL"));
        v.setInternalNotes(line.get("NOTES"));

        v = venues.save(v);
        venueMap.put(line.get("ID"), v);

        VenueContact c = new VenueContact();
        c.setVenue(v);
        c.setFormOfAddress(line.get("GENDER").equals("f")
                ? "Sehr geehrte Frau " + line.get("LASTNAME")
                : "Sehr geehrter Herr " + line.get("LASTNAME"));
        c.setGivenName(line.get("FIRSTNAME"));
        c.setFamilyName(line.get("LASTNAME"));
        c.setEmail(line.get("EMAIL"));
        c.setPhone(line.get("CONTACTPHONE"));

        contacts.save(c);
      }
    }

    try (FileReader fileReader = new FileReader(hallFile);
            CsvMapReader csvReader = new CsvMapReader(
                    fileReader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)) {

      String[] header = csvReader.getHeader(true);
      Map<String, String> line;
      while ((line = csvReader.read(header)) != null) {

        Hall h = new Hall();
        h.setVenue(venueMap.get(line.get("CINEMA_ID")));
        h.setName(line.get("NAME"));
        h.setCapacity(Integer.parseInt(line.get("SEATS")));
        h.setFilm35(line.get("FILM35").equals("1"));
        h.setFilm16(line.get("FILM16").equals("1"));
        h.setBetasp(line.get("BETASP").equals("1"));
        h.setDcp(line.get("DCP").equals("1"));
        h.setDvd(line.get("DVD").equals("1"));
        h.setBluray(line.get("BLURAY").equals("1"));
        h.setInternalNotes(line.get("NOTES"));

        halls.save(h);
      }
    }

    return true;
  }

}
