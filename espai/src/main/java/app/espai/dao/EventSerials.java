/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.filter.EventFilter;
import app.espai.model.Event;
import app.espai.model.EventSerial;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import java.util.List;
import rocks.xprs.runtime.db.DataItem;

/**
 * DAO class for eventSerial
 */
@Named
@Stateless
public class EventSerials extends AbstractEventSerials {
  
  @EJB
  private Events events;
  
  @Override
  public void delete(DataItem dataItem) {
    if (dataItem instanceof EventSerial eventSerial) {
      EventFilter eventFilter = new EventFilter();
      eventFilter.setEventSerial(eventSerial);
      
      List<Event> eventList = events.list(eventFilter).getItems();
      for (Event e : eventList) {
        e.setEventSerial(null);
        events.save(e);
      }
    }
    
    super.delete(dataItem);
  }

}