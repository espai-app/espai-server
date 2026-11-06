/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import app.espai.filter.LicenseConditionFilter;
import app.espai.model.Agency;
import app.espai.model.LicenseCondition;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO class for agency
 */
@Named
@Stateless
public class Agencies extends AbstractAgencies {
  
  @EJB
  private LicenseConditions licenseConditions;

  public Agency getByName(String name) {
    TypedQuery<Agency> query = em.createQuery(
            "SELECT a FROM Agency a WHERE a.name = :name",
            Agency.class);
    query.setParameter("name", name);

    List<Agency> result = query.getResultList();
    if (result.isEmpty()) {
      return null;
    }

    return result.get(0);
  }
  
  public void merge(Agency source, Agency target) {
     List<LicenseCondition> licenseList = getLicenses(source);
     for (LicenseCondition c : licenseList) {
       c.setAgency(target);
       licenseConditions.save(c);
     }
     
     delete(source);
  }
  
  public void delete(Agency agency) {
    List<LicenseCondition> licenseList = getLicenses(agency);
     for (LicenseCondition c : licenseList) {
       licenseConditions.delete(c);
     }
     
     super.delete(agency);
  }
  
  private List<LicenseCondition> getLicenses(Agency agency) {
    LicenseConditionFilter licenseFilter = new LicenseConditionFilter();
    licenseFilter.setAgency(agency);
    return licenseConditions.list(licenseFilter).getItems();
  }

}