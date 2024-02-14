package app.espai.views.events;

import app.espai.dao.Events;
import app.espai.dao.Halls;
import app.espai.dao.Venues;
import app.espai.filter.EventFilter;
import app.espai.model.Event;
import app.espai.model.Hall;
import app.espai.model.Venue;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.model.timeline.TimelineModel;

/**
 *
 * @author rborowski
 */
public class EventsTimelineView {

  @EJB
  private SeasonContext seasonContext;

  @EJB
  private Events events;

  private List<Event> eventList;
  private TimelineModel<Event, String> model;

  @PostConstruct
  public void init() {

    model = new TimelineModel<>();

    EventFilter eventFilter = new EventFilter();
    eventFilter.setSeason(seasonContext.getCurrentSeason());
    eventList = events.list(eventFilter).getItems();

    // collect venues and halls
    Map<Venue, List<Hall>> hallMap = new HashMap<>();
    eventList.stream()
            .map(Event::getHall)
            .distinct()
            .forEach(h -> {
              if (!hallMap.containsKey(h.getVenue())) {
                hallMap.put(h.getVenue(), new LinkedList<>());
              }
              hallMap.get(h.getVenue()).add(h);
            });

    List<Venue> venueSet = new LinkedList<>(hallMap.keySet());
    venueSet.sort(Venues.DEFAULT_ORDER);

    for (Venue v : venueSet) {
      List<String> hallIds = hallMap.get(v).stream()
              .sorted(Halls.DEFAULT_ORDER)
              .map(h -> "h" + h.getId())
              .collect(Collectors.toList());
      model.addGroup(new TimelineGroup<>("v" + v.getId(), v.getName() + ", " + v.getCity(), "v" + v.getId(), 1, hallIds));
    }

  }
}
