package app.espai.views.users;

import app.espai.dao.Users;
import app.espai.model.User;
import jakarta.annotation.Resource;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import rocks.xprs.mail.Mail;
import rocks.xprs.mail.MailClient;
import rocks.xprs.security.Pbkdf2Hash;

/**
 *
 * @author rborowski
 */
@Named
@Stateless
public class UserPasswordManager {

  @Resource(lookup = "mail/espai")
  private Session mailSession;

  @EJB
  private Users users;

  public void setPassword(User user, String password) {
    String hash = Pbkdf2Hash.createHash(password);
    user.setPassword(hash);
    users.save(user);
  }

  public boolean checkPassword(User user, String password) {
    return Pbkdf2Hash.verifyPassword(password, user.getPassword());
  }

  @Asynchronous
  public Future<Void> resetPassword(User user) {

    try {
      user.setResetToken(UUID.randomUUID().toString());
      user.setResetTokenValid(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
      users.save(user);

      Config config = ConfigProvider.getConfig();

      String resetLink = config.getValue("passwordReset.url", String.class) + "?token=" + user.getResetToken();

      MailClient client = new MailClient(mailSession);
      Mail mail = client.compose();
      mail.setFrom("noreply@schulkinowoche.de");
      mail.addTo(user.getEmail());
      mail.setSubject("Neues Passwort für die Schulkinowochen Sachsen");
      mail.setText(String.format(
              """
Guten Tag %s,
Sie erhalten diese E-Mail, weil Sie ein (neues) Passwort für das Portal der Schulkinowochen Sachsen beantragt haben. Sollten Sie kein neues Passwort benötigen, können Sie diese E-Mail einfach ignorieren.
Falls Sie ein neues Passwort setzen möchten, folgen Sie bitte diesem Link:

%s

Der Link ist 24 Stunden gültig. Sollten Sie sich innerhalb dieser Zeit kein neues Passwort gesetzt haben, können Sie sich einen neuen Link über die Passwort-vergessen-Funktion zusenden lassen.

Viele Grüße

Ihr Team der Schulkinowochen Sachsen
""",
               user.getDisplayName(), resetLink));

      client.send(mail);
    } catch (IOException | MessagingException ex) {
      Logger.getLogger(UserPasswordManager.class.getName()).log(
              Level.SEVERE, "Password reset mail not sent.", ex);
    }
    return null;

  }
}
