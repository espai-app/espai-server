/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.sdk;

import app.espai.sdk.model.ReservationDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author rborowski
 */
public class ReservationClient {

  private final HttpClient httpClient;
  private final URI applicationRoot;

  public ReservationClient(String applicationRoot) throws URISyntaxException {
    if (!applicationRoot.endsWith("/")) {
      applicationRoot += "/";
    }
    this.applicationRoot = new URI(applicationRoot);
    httpClient = HttpClient.newHttpClient();
  }

  public List<String> validate(ReservationDTO reservation) throws IOException {

    try {
      Gson gson = new Gson();
      String serializedReservation = gson.toJson(reservation);

      URI targetUri = applicationRoot.resolve("webservice/reservations/validate");

      HttpRequest validateRequest = HttpRequest.newBuilder(targetUri)
              .header("Content-Type", "application/json")
              .header("Accept", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(serializedReservation, StandardCharsets.UTF_8))
              .build();

      HttpResponse<String> validateResponse = httpClient.send(
              validateRequest,
              HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      
      if (validateResponse.statusCode() != 200) {
        throw new IOException(validateResponse.body());
      }
      
      return gson.fromJson(validateResponse.body(), new TypeToken<List<String>>() {});
    } catch (InterruptedException ex) {
      throw new IOException(ex);
    }

  }

  public boolean submit(ReservationDTO reservation) throws IOException {
    
    try {
      Gson gson = new Gson();
      String serializedReservation = gson.toJson(reservation);

      URI targetUri = applicationRoot.resolve("webservice/reservations/submit");

      HttpRequest validateRequest = HttpRequest.newBuilder(targetUri)
              .header("Content-Type", "application/json")
              .header("Accept", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(serializedReservation, StandardCharsets.UTF_8))
              .build();

      HttpResponse<String> validateResponse = httpClient.send(
              validateRequest,
              HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      
      if (validateResponse.statusCode() != 200) {
        throw new IOException(validateResponse.body());
      }
      
      return Boolean.parseBoolean(validateResponse.body());
    } catch (InterruptedException ex) {
      throw new IOException(ex);
    }

  }

}
