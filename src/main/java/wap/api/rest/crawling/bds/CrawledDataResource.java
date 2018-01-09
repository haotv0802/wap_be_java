package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import wap.api.rest.crawling.bds.beans.Contact;
import wap.api.rest.crawling.bds.beans.Criterion;
import wap.api.rest.crawling.bds.beans.ItemPresenter;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataService;
import wap.api.rest.crawling.mailing.JavaMailService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
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

  @GetMapping("/crawler/sendAds")
  public List<Contact> sendAds(
  ) throws IOException, MessagingException {
//    List<String> emails = this.crawledDataService.getEmails();
      List<Contact> contacts = this.crawledDataService.getContacts();

      Contact contact = new Contact();
      contact.setId(new Long (8973));
      contact.setName("HELLO");
      contact.setPhone("0906729772");
      contact.setEmail("ahmobilepicture01@gmail.com");
      contact.setType("OWNER");
      contact.setUrl("https://batdongsan.com.vn/ban-nha-mat-pho-duong-nguyen-van-cu-1-xa-phuoc-an-1-prj-khu-do-thi-detaco-nhon-trach/5x20-tret-lau-d-dta-lh-0908-759-337-pr12456934");
      contact.setLatestItemPostedOn(new Date());
    JavaMailService.sendAdsToContacts(contact);
//    JavaMailService.sendMailOfAds("hoanhhao@gmail.com");
//    for(Contact contact : contacts) {
//      JavaMailService.sendMailOfAds(email);
//    }

    return contacts;
  }

}