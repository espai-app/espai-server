/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.webservice;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author rborowski
 */
@Path("/suggest/address")
public class AddressWrapperWebservice {

  @Inject
  @ConfigProperty(name = "espai.solr.address")
  private String solrUrl;

  @GET
  public Response lookup(@Context HttpServletRequest request, @QueryParam("q") String query) {
    try {
      String cleanQuery = URLEncoder.encode(
              query.replaceAll("&\\?\\:\\*", ""), 
              StandardCharsets.UTF_8);
      InputStream searchResult = URI.create(
              String.format("%s/select?df=name&q=%s*", 
                      solrUrl, 
                      cleanQuery))
              .toURL()
              .openStream();

      return Response
              .ok(searchResult, MediaType.APPLICATION_JSON)
              .header("Access-Control-Allow-Origin", "*")
              .build();
    } catch (IOException ex) {
      Logger.getLogger(AddressWrapperWebservice.class.getName())
              .log(Level.SEVERE, "Error querying address index.", ex);
      return Response.serverError().build();
    }
  }

}
