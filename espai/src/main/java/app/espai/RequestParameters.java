/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai;

import jakarta.faces.context.FacesContext;

/**
 *
 * @author rborowski
 */
public class RequestParameters {
  
  public static String getParameter(String parameterName) {
    return FacesContext.getCurrentInstance()
            .getExternalContext()
            .getRequestParameterMap()
            .get(parameterName);
  }
  
  public static Long getParameterAsLong(String parameterName) {
    String param = getParameter(parameterName);
    if (param != null && param.matches("\\d+")) {
      return Long.valueOf(param);
    }
    return null;
  }
  
}
