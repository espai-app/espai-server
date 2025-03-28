/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.sdk;

import app.espai.sdk.model.ReservationDTO;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 *
 * @author rborowski
 */
public class ReservationIT {
  
  @Test
  public void testSubmitReservation() throws URISyntaxException, IOException {
    
    ReservationDTO reservation = new ReservationDTO();
    reservation.setAcceptTos(true);
    reservation.setCompany("Baumschule \"Zur Eibe\"");
    reservation.setGivenName("Erika");
    reservation.setSurname("Buchsbaum");
    reservation.setAddress("An der Eibe 1");
    reservation.setPostcode("01234");
    reservation.setCity("Buchen");
    reservation.setPhone("01234 56789");
    
    reservation.setEvent(8626L);
    
    HashMap<Long, Integer> ticketMap = new HashMap<>();
    ticketMap.put(10267L, 20);
    ticketMap.put(10268L, 2);
    ticketMap.put(10269L, 2);
    
    reservation.setTickets(ticketMap);
    
    reservation.setMessage("Wir freuen uns schon!");
    
    ReservationClient client = new ReservationClient("http://localhost:8080/espai");
    List<String> errors = client.validate(reservation);
    
    assert(errors.isEmpty());
    
    boolean success = client.submit(reservation);
    assert(success);
  }
  
}
