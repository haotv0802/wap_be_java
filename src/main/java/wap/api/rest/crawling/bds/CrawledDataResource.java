package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import wap.api.rest.crawling.bds.beans.*;
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
      Pageable pageable
  ) {
    return crawledDataService.getAllItems(pageable);
  }

  @GetMapping("/crawler/crawledData/{type}")
  public List<ItemPresenter> getCrawledDataAsSale(
      @PathVariable ("type") String type,
      Pageable pageable
  ) {
    LOGGER.info("{type} service");
    return crawledDataService.getAllItems(pageable, type);
  }


  @GetMapping("/crawler/crawledData/{type}/count")
  public Integer getCrawledDataAsSaleAndCount(
      @PathVariable ("type") String type,
      Pageable pageable
  ) {
    LOGGER.info("{type} count service");
    return crawledDataService.getAllItems(pageable, type).size();
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

  @GetMapping("/crawler/exportContactsToFiles")
  public void exportContactsToFiles(
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "city", required = false) String city,
      @RequestParam(value = "noOfPosts", required = false) Integer noOfPosts,
      @RequestParam(value = "year", required = false) Integer year,
      @RequestParam(value = "month", required = false) Integer month
  ) throws IOException {
    for(int i = 1; i <= noOfPosts; i++) {
      this.crawledDataService.exportContacts(email, city, i, year, month);
    }
  }

  @GetMapping("/crawler/exportAllContactsToExcelFiles")
  public void exportAllContactsToExcelFiles(
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "city", required = false) String city,
      @RequestParam(value = "noOfPosts", required = false) Integer noOfPosts,
      @RequestParam(value = "onlyNewData", required = false) Boolean onlyNewData
  ) throws IOException {
    for(int i = 1; i <= noOfPosts; i++) {
      this.crawledDataService.exportContacts(email, city, i, onlyNewData);
    }
  }

  @GetMapping("/crawler/exportAllPhonesAndEmailsToExcelFiles")
  public void exportAllPhonesAndEmailsToExcelFiles(
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "city", required = false) String city,
      @RequestParam(value = "noOfPosts", required = false) Integer noOfPosts,
      @RequestParam(value = "onlyNewData", required = false) Boolean onlyNewData
  ) throws IOException {
    for(int i = 1; i <= noOfPosts; i++) {
      this.crawledDataService.exportPhonesAndEmails(email, city, i, onlyNewData);
    }
  }

  @GetMapping("/crawler/exportAllPhonesAndEmailsToCSVFiles")
  public void exportAllPhonesAndEmailsToCSVFiles(
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "city", required = false) String city,
      @RequestParam(value = "noOfPosts", required = false) Integer noOfPosts,
      @RequestParam(value = "onlyNewData", required = false) Boolean onlyNewData
  ) throws IOException {
    for(int i = 1; i <= noOfPosts; i++) {
      this.crawledDataService.exportPhonesAndEmailsToCSVFiles(email, city, i, onlyNewData);
    }
  }

  @GetMapping("/crawler/testEmail")
  public void testEmail() throws IOException, MessagingException, InterruptedException {
    this.crawledDataService.testEmail();
  }

  @GetMapping("/crawler/sendAdsToCustomers")
  public void sendAdsToCustomers() throws MessagingException, InterruptedException {
    this.crawledDataService.sendAdsToCustomers();
  }

  @GetMapping("/crawler/sendAdsBlockEToCustomers")
  public void sendAdsBlockEToCustomers() throws MessagingException, InterruptedException {
    this.crawledDataService.sendAdsBlockEToCustomers();
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

  @GetMapping("/crawler/citiesAndDistricts")
  public List<CityPresenter> getCitiesAndDistricts() {
    return this.crawledDataService.getCitiesAndDistricts();
  }
}