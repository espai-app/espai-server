
package app.espai.events;

import rocks.xprs.runtime.db.DataItem;

/**
 *
 * @author rborowski
 */
public class ItemChangeEvent {

  public enum Type {CREATED, UPDATED, DELETED}

  private DataItem dataItem;
  private Type type;

  public ItemChangeEvent() {

  }

  public ItemChangeEvent(DataItem dataItem, Type type) {
    this.dataItem = dataItem;
    this.type = type;
  }

  public boolean isInstanceOf(Class<? extends DataItem> clazz) {
    return dataItem != null && dataItem.getClass().isAssignableFrom(clazz);
  }

  //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
  /**
   * @return the dataItem
   */
  public DataItem getDataItem() {
    return dataItem;
  }

  /**
   * @param dataItem the dataItem to set
   */
  public void setDataItem(DataItem dataItem) {
    this.dataItem = dataItem;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(Type type) {
    this.type = type;
  }
  //</editor-fold>


}
