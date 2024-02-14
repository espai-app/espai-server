/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.model;

import jakarta.persistence.Entity;

/**
 * Model class for productionVersion
 */
@Entity
public class ProductionVersion extends AbstractProductionVersion {

  private static final long serialVersionUID = 1L;

  public String getFullName() {
    String fullname = this.getProduction().getTitle();
    if (this.getVersionName() != null && !this.getVersionName().isBlank()) {
      fullname += " (" + this.getVersionName() + ")";
    }
    return fullname;
  }

}