/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */
package app.espai.model;

import static app.espai.model.FieldType.DATE;
import static app.espai.model.FieldType.INTEGER;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Model class for productionMetaValue
 */
@Entity
public class ProductionMetaValue extends AbstractProductionMetaValue {

  private static final long serialVersionUID = 1L;

  public Object getFormattedValue() {

    if (getValue() == null) {
      return null;
    }

    Gson gson = new Gson();

    try {
      switch (getProductionMetaField().getFieldType()) {
        case INTEGER:
          return Integer.parseInt(getValue());
        case DECIMAL:
          return new BigDecimal(getValue());
        case DATE:
          return LocalDate.parse(getValue());
        case DATETIME:
          return LocalDateTime.parse(getValue());
        case BOOLEAN:
          return Boolean.parseBoolean(getValue());
        case TAG:
          return gson.fromJson(getValue(), List.class);
        default:
          return getValue();
      }
    } catch (NumberFormatException | JsonSyntaxException | DateTimeParseException ex) {
      return null;
    }
  }

  public void setFormattedValue(Object object) {

    if (object == null || (object instanceof String s && s.isBlank()) ) {
      setValue(null);
      return;
    }
    
    Gson gson = new Gson();
    
    switch (getProductionMetaField().getFieldType()) {
      case DATE:
        setValue(((LocalDate) object).toString());
        break;
      case DATETIME:
        setValue(((LocalDateTime) object).toString());
        break;
      case TAG:
        setValue(gson.toJson(object));
        break;
      default:
        setValue(String.valueOf(object));
        break;
    }
  }
  
  public static class Role {
    
    private String role;
    private String artist;

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    /**
     * @return the role
     */
    public String getRole() {
      return role;
    }
    
    /**
     * @param role the role to set
     */
    public void setRole(String role) {
      this.role = role;
    }
    
    /**
     * @return the artist
     */
    public String getArtist() {
      return artist;
    }
    
    /**
     * @param artist the artist to set
     */
    public void setArtist(String artist) {
      this.artist = artist;
    }
    //</editor-fold>
    
  }

}
