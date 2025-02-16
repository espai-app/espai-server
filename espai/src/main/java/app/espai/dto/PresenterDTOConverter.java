/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.dto;

import app.espai.model.Presenter;
import app.espai.sdk.model.PresenterDTO;

/**
 *
 * @author rborowski
 */
public class PresenterDTOConverter {
  
  public static PresenterDTO of(Presenter presenter) {
    PresenterDTO result = new PresenterDTO();
    result.setId(presenter.getId());
    result.setGivenName(presenter.getGivenName());
    result.setSurname(presenter.getSurname());

    return result;
  }
  
}
