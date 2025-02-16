/*
 * Copyright 2023 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.espai.auth;

import app.espai.dao.AccessRights;
import app.espai.dao.Users;
import app.espai.filter.AccessRightFilter;
import app.espai.model.AccessRight;
import app.espai.model.User;
import app.espai.views.users.UserPasswordManager;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.util.List;
import java.util.Set;
import rocks.xprs.runtime.exceptions.ResourceNotFoundException;

/**
 *
 * @author borowski
 */
@ApplicationScoped
public class DatabaseIdentityStore implements IdentityStore {

  @EJB
  private Users users;

  @EJB
  private AccessRights accessRights;

  @EJB
  private UserPasswordManager userPasswordManager;

  @Override
  public CredentialValidationResult validate(Credential credential) {

    if (!(credential instanceof UsernamePasswordCredential)) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    UsernamePasswordCredential userPass = (UsernamePasswordCredential) credential;

    try {
      User user = users.getByUsername(userPass.getCaller());
      if (user != null && userPasswordManager.checkPassword(user, userPass.getPasswordAsString())) {
        AccessRightFilter rightFilter = new AccessRightFilter();
        rightFilter.setUser(user);
        List<AccessRight> accessRightList = accessRights.list(rightFilter).getItems();

        EspaiPrincipal principal = new EspaiPrincipal(user, accessRightList);

        Set<String> roles = principal.getRoles();
        if (!roles.isEmpty()) {
          return new CredentialValidationResult(principal, roles);
        }
      }
    } catch (ResourceNotFoundException ex) {
      // ignore
    }
    return CredentialValidationResult.INVALID_RESULT;
  }
}
