package app.espai;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import java.io.IOException;
import rocks.xprs.mail.ImapClient;
import rocks.xprs.mail.Mail;
import rocks.xprs.mail.MailClient;

/**
 *
 * @author rborowski
 */
@Stateless
public class Mailer {

  @Resource(lookup = "mail/espai")
  private Session smtpSession;

  @Resource(lookup = "mail/espai-imap")
  private Session imapSession;

  public Message send(Mail mail) throws MessagingException, IOException {
    if (!mail.getHtml().contains("<html")) {
      mail.setHtml("<html><head><style type=\"text/css\">p {margin: 0; padding: 0;}</style>" + mail.getHtml() + "</html>");
    }
    MailClient mailClient = new MailClient(smtpSession);
    return mailClient.send(mail);
  }

  public void saveSent(Message mail) throws MessagingException {
    ImapClient imapClient = new ImapClient(
            imapSession,
            imapSession.getProperty("mail.imaps.user"),
            imapSession.getProperty("mail.imaps.password"));
    imapClient.save(imapSession.getProperty("mail.imaps.folders.sent"), mail);
  }
}
