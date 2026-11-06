/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.converter;

import app.espai.model.ProductionMetaValue;
import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;

/**
 *
 * @author rborowski
 */
@Named
@ApplicationScoped
@FacesConverter(value="productionMetaValueJsonConverter", managed = true)
public class ProductionMetaValueJsonConverter implements Converter<ProductionMetaValue> {
  
  private final Gson gson = new Gson();

  @Override
  public ProductionMetaValue getAsObject(FacesContext context, UIComponent component, String value) {
    return gson.fromJson(value, ProductionMetaValue.class);
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, ProductionMetaValue value) {
    return gson.toJson(value);
  }
  
}
