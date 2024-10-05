/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */
package app.espai.dao;

import app.espai.events.SeatCategoryChangedEvent;
import app.espai.model.SeatCategory;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * DAO class for seatCategory
 */
@Named
@Stateless
public class SeatCategories extends AbstractSeatCategories {

  @Inject
  private Event<SeatCategoryChangedEvent> seatChangedEvent;

  @Override
  public SeatCategory save(SeatCategory seatCategory) {
    seatCategory = super.save(seatCategory);
    seatChangedEvent.fire(new SeatCategoryChangedEvent(seatCategory));
    return seatCategory;
  }

  public void delete(SeatCategory seatCategory) {
    super.delete(seatCategory);
    seatChangedEvent.fire(new SeatCategoryChangedEvent(seatCategory));
  }
}
