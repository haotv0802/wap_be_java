package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import wap.api.rest.crawling.bds.beans.Contact;
import wap.api.rest.crawling.bds.beans.ContactPresenter;
import wap.api.rest.crawling.bds.beans.Criterion;
import wap.api.rest.crawling.bds.beans.ItemPresenter;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataService;
import wap.api.rest.crawling.mailing.JavaMailService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * Date: 12/21/2017
 *
 * @author haho
 */
@RestController("bdsCrawledResource")
@RequestMapping(path = "/svc/bds")
public class CrawledDataResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  private final ICrawledDataService crawledDataService;

  public CrawledDataResource(
      @Qualifier("bdsCrawledDataService") ICrawledDataService crawledDataService
  ) {
    Assert.notNull(crawledDataService);

    this.crawledDataService = crawledDataService;
  }

  @GetMapping("/crawler/crawledData")
  public List<ItemPresenter> getCrawledData(
  ) {
    return crawledDataService.getAllItems();
  }

  @PostMapping("/crawler/crawledData")
  public List<ItemPresenter> getCrawledDataByCriterion(
      @RequestBody Criterion criterion
  ) {
    return crawledDataService.getAllItemsByCriterion(criterion);
  }

  @PostMapping("/crawler/exportCrawledData")
  public List<ItemPresenter> exportCrawledData(
      @RequestBody Criterion criterion
  ) throws IOException {
    return crawledDataService.exportCrawledData(criterion);
  }

  @PostMapping("/crawler/exportCrawledDataManually")
  public List<ContactPresenter> exportCrawledDataManually(
      @RequestBody Criterion criterion
  ) throws IOException {
    return crawledDataService.getPostsManually(criterion);
  }

  @GetMapping("/crawler/exportOwnerContacts")
  public void exportOwnerContacts() throws IOException {
    this.crawledDataService.exportOwnerContacts();
  }

  @GetMapping("/crawler/testEmail")
  public void testEmail() throws IOException, MessagingException {
    this.crawledDataService.testEmail();
  }

  @GetMapping("/crawler/sendAds")
  public List<Contact> sendAds(
  ) throws IOException, MessagingException {
    List<Contact> contacts = this.crawledDataService.getContacts();

    for (Contact contact : contacts) {
      JavaMailService.sendAdsToContacts(contact);
    }

    return contacts;
  }

}