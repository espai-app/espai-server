/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.model.Venue;
import app.espai.sdk.model.VenueDTO;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class VenueDTOConverter {

  public VenueDTO toDTO(Venue venue) {

    if (venue == null) {
      return null;
    }

    VenueDTO result = new VenueDTO();

    result.setId(venue.getId());
    result.setName(venue.getName());
    result.setAddress(venue.getAddress());
    result.setPostcode(venue.getPostcode());
    result.setCity(venue.getCity());
    result.setPhone(venue.getPhone());
    result.setEmail(venue.getEmail());
    result.setPublicNotes(venue.getPublicNotes());
    result.setSlug(venue.getSlug());
    result.setLatitude(venue.getLatitude());
    result.setLongitude(venue.getLongitude());

    return result;
  }

}
