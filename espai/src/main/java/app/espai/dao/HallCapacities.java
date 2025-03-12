/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.events.HallCapacityChangedEvent;
import app.espai.model.HallCapacity;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * DAO class for hallCapacity
 */
@Named
@Stateless
public class HallCapacities extends AbstractHallCapacities {
  
  @Inject
  private Event<HallCapacityChangedEvent> hallCapacityChangedEvent;

  @Override
  public HallCapacity save(HallCapacity hallCapacity) {
    hallCapacity = super.save(hallCapacity);
    hallCapacityChangedEvent.fire(new HallCapacityChangedEvent(hallCapacity));
    return hallCapacity;
  }

  public void delete(HallCapacity seatCategory) {
    super.delete(seatCategory);
    hallCapacityChangedEvent.fire(new HallCapacityChangedEvent(seatCategory));
  }

}