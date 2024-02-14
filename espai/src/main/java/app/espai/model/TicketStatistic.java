package app.espai.model;

/**
 *
 * @author rborowski
 */
public class TicketStatistic {

  private Event event;
  private Long ticketsSold;

  public TicketStatistic(Event event, Long ticketsSold) {
    this.event = event;
    this.ticketsSold = ticketsSold;
  }

  /**
   * @return the event
   */
  public Event getEvent() {
    return event;
  }

  /**
   * @param event the event to set
   */
  public void setEvent(Event event) {
    this.event = event;
  }

  /**
   * @return the ticketsSold
   */
  public Long getTicketsSold() {
    return ticketsSold;
  }

  /**
   * @param ticketsSold the ticketsSold to set
   */
  public void setTicketsSold(Long ticketsSold) {
    this.ticketsSold = ticketsSold;
  }

}
