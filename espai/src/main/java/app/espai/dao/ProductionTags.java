/**
 * This software was created with xprs.
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.dao;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;

/**
 * DAO class for productionTag
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ProductionTags extends AbstractProductionTags {

}