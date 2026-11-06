
package app.espai.webservice.auth;

import app.espai.auth.EspaiPrincipal;
import app.espai.model.UserRole;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 *
 * @author rborowski
 */
@Provider
@RequireManager
public class RequireManagerFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {

    EspaiPrincipal principal = (EspaiPrincipal) requestContext.getSecurityContext()
            .getUserPrincipal();

    if (!principal.hasAccess(UserRole.MANAGER, null)) {
      requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
    }
  }

}
