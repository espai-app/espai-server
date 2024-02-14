package app.espai.webservice;

import app.espai.dao.AccessRights;
import app.espai.dao.Users;
import app.espai.dao.VenueContacts;
import app.espai.model.AccessRight;
import app.espai.model.User;
import app.espai.model.UserRole;
import app.espai.model.VenueContact;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;

/**
 *
 * @author rborowski
 */
@Path("/accounts")
public class UserAccountsWebservice {

  @EJB
  private Users users;

  @EJB
  private AccessRights accessRights;

  @EJB
  private VenueContacts contacts;

  @GET
  @Path("createFromContacts")
  public String createAccounts() {

    List<? extends VenueContact> contactList = contacts.list().getItems();

    for (VenueContact c : contactList) {

      User u = users.getByUsername(c.getEmail());
      if (u != null) {
        continue;
      }

      u = new User();
      u.setUsername(c.getEmail());
      u.setDisplayName(c.getGivenName() + " " + c.getFamilyName());
      u.setEmail(c.getEmail());
      u = users.save(u);

      AccessRight right = new AccessRight();
      right.setUser(u);
      right.setRole(UserRole.VENUE_MANAGER);
      right.setVenue(c.getVenue());
      accessRights.save(right);
    }

    return "OK";
  }
}
