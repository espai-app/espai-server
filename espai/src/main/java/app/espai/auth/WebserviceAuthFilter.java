/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.espai.auth;

import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.RememberMeCredential;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author rborowski
 */
public class WebserviceAuthFilter implements Filter {

  @Inject
  private SecurityContext securityContext;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if (securityContext.getCallerPrincipal() != null
            || httpRequest.getRequestURI().contains("webservice/attachments")) { // TODO!
      chain.doFilter(request, response);
      return;
    }

    String authorizationHeader = httpRequest.getHeader("Authorization");
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      httpResponse.setHeader("WWW-Authenticate", "Bearer");
      return;
    }

    String apiToken = new String(authorizationHeader.substring(6).trim());

    RememberMeCredential credential = new RememberMeCredential(apiToken);
    AuthenticationParameters params = new AuthenticationParameters();
    params.setCredential(credential);
    params.setNewAuthentication(true);

    AuthenticationStatus status = securityContext.authenticate(
            httpRequest, httpResponse, params);

    if (status != AuthenticationStatus.SUCCESS) {
      httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      httpResponse.setHeader("WWW-Authenticate", "Bearer");
      return;
    }

    chain.doFilter(request, response);

  }
}
