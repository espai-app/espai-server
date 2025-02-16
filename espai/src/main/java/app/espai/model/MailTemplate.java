/**
 * This software was created with xprs. 
 * Have a look at https://xprs.rocks/ for more details.
 */

package app.espai.model;

import jakarta.persistence.Entity;

/**
 * Model class for mailTemplate
 */
@Entity
public class MailTemplate extends AbstractMailTemplate {

  private static final long serialVersionUID = 1L;
  
  public MailTemplate duplicate() {
    MailTemplate result = new MailTemplate();
    result.setBcc(getBcc());
    result.setDescription(getDescription());
    result.setHtmlMessage(getHtmlMessage());
    result.setName(getName());
    result.setReplyTo(getReplyTo());
    result.setSender(getSender());
    result.setShortCode(getShortCode());
    return result;
  }
  
}