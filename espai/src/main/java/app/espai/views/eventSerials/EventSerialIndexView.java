/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.views.eventSerials;

import app.espai.dao.EventSerials;
import app.espai.dao.Events;
import app.espai.filter.EventFilter;
import app.espai.filter.EventSerialFilter;
import app.espai.model.Event;
import app.espai.model.EventSerial;
import app.espai.views.Dialog;
import app.espai.views.SeasonContext;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class EventSerialIndexView {
  
  @EJB
  private SeasonContext seasonContext;
  
  @EJB
  private EventSerials eventSerials;
  
  @EJB
  private Events events;
  
  private List<EventSerial> eventSerialList;
  private Map<Long, Integer> eventCounterMap = new HashMap<>();
  
  @PostConstruct
  public void init() {
    EventSerialFilter serialFilter = new EventSerialFilter();
    serialFilter.setSeason(seasonContext.getCurrentSeason());
    eventSerialList = eventSerials.list(serialFilter).getItems();
    
    EventFilter eventFilter = new EventFilter();
    eventFilter.setEventSerials(eventSerialList);
    events.list(eventFilter).getItems()
            .stream()
            .collect(Collectors.groupingBy(Event::getEventSerial))
            .forEach((key, list) -> eventCounterMap.put(key.getId(), list.size()));
  }
  
  public void add() {
    Map<String, List<String>> params = new HashMap<>();
    params.put("seasonId", List.of(String.valueOf(seasonContext.getCurrentSeason().getId())));
    
    PrimeFaces.current().dialog().openDynamic(
            "eventSerialEditor", 
            Dialog.getDefaultOptions(400, 250), 
            params);
  }
  
  public void edit(EventSerial serial) {
    Map<String, List<String>> params = new HashMap<>();
    params.put("eventSerialId", List.of(String.valueOf(serial.getId())));
    
    PrimeFaces.current().dialog().openDynamic(
            "eventSerialEditor", 
            Dialog.getDefaultOptions(400, 250), 
            params);
  }
  
  public void delete(EventSerial serial) {
    eventSerials.delete(serial);
    init();
  }
  
  public void onDataChanged(SelectEvent<Object> event) {
    init();
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the eventSerialList
   */
  public List<EventSerial> getEventSerialList() {
    return eventSerialList;
  }
  
  /**
   * @param eventSerialList the eventSerialList to set
   */
  public void setEventSerialList(List<EventSerial> eventSerialList) {
    this.eventSerialList = eventSerialList;
  }
  
  /**
   * @return the eventCounterMap
   */
  public Map<Long, Integer> getEventCounterMap() {
    return eventCounterMap;
  }

  /**
   * @param eventCounterMap the eventCounterMap to set
   */
  public void setEventCounterMap(Map<Long, Integer> eventCounterMap) {
    this.eventCounterMap = eventCounterMap;
  }
  //</editor-fold>

}
