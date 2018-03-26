package wap.api.rest.crawling.bds.interfaces;

import javax.mail.MessagingException;

/**
 * Date: 10/20/2017 Time: 10:18 AM
 *
 * @author haho
 */
public interface IEmailsService {
  public void sendAdsEmeraldToCustomers() throws MessagingException;
}
