package app.espai;

import app.espai.model.MailAccount;
import app.espai.model.MailTransportSecurity;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import java.io.IOException;
import java.util.Properties;
import rocks.xprs.mail.ImapClient;
import rocks.xprs.mail.Mail;
import rocks.xprs.mail.MailClient;

/**
 *
 * @author rborowski
 */
public class Mailer {

  private final MailAccount mailAccount;
  private final Session mailSession;

  public Mailer(MailAccount mailAccount) {

    this.mailAccount = mailAccount;

    Properties sessionProperties = new Properties();
    String smtpProtocol = mailAccount.getSmtpTransportSecurity() == MailTransportSecurity.SSL
            ? "smpts" : "smtp";
    String imapProtocol = mailAccount.getSmtpTransportSecurity() == MailTransportSecurity.SSL
            ? "imaps" : "imap";

    sessionProperties.put("mail.transport.protocol", smtpProtocol);
    sessionProperties.put("mail." + smtpProtocol + ".host", mailAccount.getSmtpHost());
    sessionProperties.put("mail." + smtpProtocol + ".port", String.valueOf(mailAccount.getSmtpPort()));
    if (mailAccount.getSmtpUser() != null && !mailAccount.getSmtpUser().isBlank()) {
        sessionProperties.put("mail." + smtpProtocol + ".user", mailAccount.getSmtpUser());
        sessionProperties.put("mail." + smtpProtocol + ".password", mailAccount.getSmtpPassword());
        sessionProperties.put("mail." + smtpProtocol + ".auth", "true");
    }

    if (mailAccount.getSmtpTransportSecurity() == MailTransportSecurity.START_TLS) {
      sessionProperties.put("mail." + smtpProtocol + ".startTls", "true");
    }

    sessionProperties.put("mail.store.protocol", imapProtocol);
    sessionProperties.put("mail." + imapProtocol + ".host", mailAccount.getSmtpHost());
    sessionProperties.put("mail." + imapProtocol + ".port", String.valueOf(mailAccount.getSmtpPort()));
    if (mailAccount.getSmtpUser() != null && !mailAccount.getSmtpUser().isBlank()) {
      sessionProperties.put("mail." + imapProtocol + ".user", mailAccount.getSmtpUser());
      sessionProperties.put("mail." + imapProtocol + ".password", mailAccount.getSmtpPassword());
      sessionProperties.put("mail." + imapProtocol + ".auth", "true");
    }
    if (mailAccount.getImapTransportSecurity() == MailTransportSecurity.SSL) {
      sessionProperties.put("mail." + smtpProtocol + ".ssl.enable", "true");
    }

    mailSession = Session.getInstance(sessionProperties);
  }

  public Message send(Mail mail) throws MessagingException, IOException {
    if (!mail.getHtml().contains("<html")) {
      mail.setHtml("<html><head><style type=\"text/css\">p {margin: 0; padding: 0;}</style>" + mail.getHtml() + "</html>");
    }
    MailClient mailClient = new MailClient(mailSession);
    return mailClient.send(mail);
  }

  public void saveSent(Message mail) throws MessagingException, IOException {
    try (ImapClient imapClient = new ImapClient(
            mailSession,
            mailAccount.getImapUser(),
            mailAccount.getImapPassword())) {
      imapClient.save(mailAccount.getImapSentFolder(), mail);
    }
  }
}
