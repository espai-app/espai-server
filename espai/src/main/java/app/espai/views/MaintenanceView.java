
package app.espai.views;

import app.espai.search.ProductionIndexManager;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.IOException;

/**
 *
 * @author rborowski
 */
@Named
@RequestScoped
public class MaintenanceView {

  @EJB
  private ProductionIndexManager productionIndexManager;

  public void reindexProductions() throws IOException {
    productionIndexManager.reindex();
  }

}
