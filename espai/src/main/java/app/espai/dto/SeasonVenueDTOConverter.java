/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.model.SeasonVenue;
import app.espai.model.Venue;
import app.espai.sdk.model.SeasonVenueDTO;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author rborowski
 */
@ApplicationScoped
public class SeasonVenueDTOConverter {

  public SeasonVenueDTO toDTO(SeasonVenue seasonVenue) {
    SeasonVenueDTO result = new SeasonVenueDTO();

    Venue venue = seasonVenue.getVenue();
    result.setId(venue.getId());
    result.setName(venue.getName());
    result.setAddress(venue.getAddress());
    result.setPostcode(venue.getPostcode());
    result.setCity(venue.getCity());
    result.setPhone(venue.getPhone());
    result.setEmail(venue.getEmail());
    result.setPublicNotes(venue.getPublicNotes());
    result.setSeasonNotes(seasonVenue.getSeasonNotes());
    result.setSlug(venue.getSlug());
    result.setAllowFreeBooking(seasonVenue.isAllowFreeBooking());
    result.setLatitude(venue.getLatitude());
    result.setLongitude(venue.getLongitude());

    return result;
  }

}
