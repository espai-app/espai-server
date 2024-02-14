/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.model;

import jakarta.persistence.Entity;

/**
 * Model class for productionTag
 */
@Entity
public class ProductionTag extends AbstractProductionTag {

  private static final long serialVersionUID = 1L;

  public ProductionTag() {

  }

  public ProductionTag(Production production, String category, String value) {
    setProduction(production);
    setCategory(category);
    setValue(value);
  }

}