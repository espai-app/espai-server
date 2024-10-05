/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.businesslogic;

import app.espai.dao.Halls;
import app.espai.dao.SeatCategories;
import app.espai.events.SeatCategoryChangedEvent;
import app.espai.filter.SeatCategoryFilter;
import app.espai.model.Hall;
import app.espai.model.SeatCategory;
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
  private SeatCategories seatCategories;

  public void updateHallCapacity(@Observes(during = TransactionPhase.AFTER_SUCCESS) SeatCategoryChangedEvent event) {

    Hall hall = event.getSeatCategory().getHall();
    SeatCategoryFilter seatFilter = new SeatCategoryFilter();
    seatFilter.setHall(hall);
    seatFilter.setPartOfTotalCapacity(true);
    List<SeatCategory> seatCategoryList = seatCategories.list(seatFilter).getItems();

    int capacity = 0;
    for (SeatCategory s : seatCategoryList) {
      capacity += s.getCapacity() != null ? s.getCapacity() : 0;
    }

    hall.setCapacity(capacity);
    halls.save(hall);
  }

}
