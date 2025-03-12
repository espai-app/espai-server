/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.businesslogic;

import app.espai.dao.HallCapacities;
import app.espai.dao.Halls;
import app.espai.events.HallCapacityChangedEvent;
import app.espai.filter.HallCapacityFilter;
import app.espai.model.Hall;
import app.espai.model.HallCapacity;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Stateless
public class HallCapacityManager {

  @EJB
  private Halls halls;

  @EJB
  private HallCapacities hallCapacities;

  public void updateHallCapacity(@Observes(during = TransactionPhase.AFTER_SUCCESS) HallCapacityChangedEvent event) {

    Hall hall = event.getHallCapacity().getHall();
    HallCapacityFilter capacityFilter = new HallCapacityFilter();
    capacityFilter.setHall(hall);
    capacityFilter.setPartOfTotalCapacity(true);
    List<HallCapacity> capacityList = hallCapacities.list(capacityFilter).getItems();

    int capacity = 0;
    for (HallCapacity s : capacityList) {
      capacity += s.getCapacity() != null ? s.getCapacity() : 0;
    }

    hall.setCapacity(capacity);
    halls.save(hall);
  }

}
