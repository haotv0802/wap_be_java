package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wap.api.rest.crawling.bds.interfaces.IEmailsService;

import javax.mail.MessagingException;

/**
 * Date: 12/21/2017
 *
 * @author haho
 */
@RestController("bdsEmailsResource")
@RequestMapping(path = "/svc/bds/emails")
public class EmailsResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  private final IEmailsService emailsService;

  public EmailsResource(
      @Qualifier("bdsEmailsService") IEmailsService emailsService
  ) {
    Assert.notNull(emailsService);

    this.emailsService = emailsService;
  }

  @GetMapping("/sendAdsEmeraldToCustomers")
  public void sendAdsEmeraldToCustomers() throws MessagingException, InterruptedException {
    this.emailsService.sendAdsEmeraldToCustomers();
  }
}