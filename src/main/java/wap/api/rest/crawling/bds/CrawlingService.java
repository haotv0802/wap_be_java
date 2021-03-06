package wap.api.rest.crawling.bds;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.beans.Contact;
import wap.api.rest.crawling.bds.beans.CrawlingTracking;
import wap.api.rest.crawling.bds.beans.Item;
import wap.api.rest.crawling.bds.interfaces.ICrawlingDao;
import wap.api.rest.crawling.bds.interfaces.ICrawlingService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Date: 12/21/2017
 *
 * @author haho
 */
@Service("bdsCrawlingService")
public class CrawlingService implements ICrawlingService {

  private final ICrawlingDao crawlingDao;

  private final BusinessService businessService;

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Autowired
  public CrawlingService(@Qualifier("bdsCrawlingDao") ICrawlingDao crawlingDao,
                         @Qualifier("bdsBusinessService") BusinessService businessService) {
    Assert.notNull(crawlingDao);
    Assert.notNull(businessService);

    this.crawlingDao = crawlingDao;
    this.businessService = businessService;
  }

  @Override
  public Map<String, CrawlingTracking> saveCrawledData(List<String> pages, boolean recrawl) {

    Map<String, CrawlingTracking> crawlingTrackingMap = new HashMap<>();
    for (String page : pages) {
      getTrackingAndItems(page, crawlingTrackingMap, recrawl);
    }

    Set<String> keys = crawlingTrackingMap.keySet();
    for (String key : keys) {
      CrawlingTracking crawlingTracking = crawlingTrackingMap.get(key);
      int itemsAddedCount = 0;
      Set<Item> items = crawlingTracking.getItems();
      if (null != items && items.size() > 0) {
        for (Item item: items) {
          // Saving Product
          Long itemId = crawlingDao.isItemExisting(item.getUrl(), item.getPublishDate(), item.getEndDate());
          //        location
          Long locationId = this.crawlingDao.isLocationExisting(item.getDistrict(), item.getCity());
          if (StringUtils.isEmpty(item.getCity()) && StringUtils.isEmpty(item.getDistrict())) {
            locationId = (long) -1;
          } else if (locationId < 0) {
            locationId = this.crawlingDao.addLocation(item.getDistrict(), item.getCity());
          }
          item.setLocationId(locationId < 0 ? null : locationId);

          //        contact
          Contact contact = new Contact();
          contact.setName(item.getContactName() == null ? "" : StringUtils.stripAccents(item.getContactName()));
          contact.setEmail(item.getContactEmail() == null ? "" : item.getContactEmail());
          contact.setPhone(item.getContactNumber() == null ? "" : item.getContactNumber());

          contact.setType("OWNER");
          if (businessService.isSale(contact.getName(), contact.getEmail())) {
            contact.setType("SALE");
          }

          contact.setLatestItemPostedOn(item.getPublishDate());
          Long contactId = this.crawlingDao.isContactExisting(contact.getPhone(), contact.getEmail());
          if (contactId > 0) {
            contact.setId(contactId);
            Contact contactTemp = this.crawlingDao.getContactById(contactId);
            if (!contact.getType().equals("SALE")) {
              if ((contactTemp.getManualCheck() != null && contactTemp.getManualCheck().equals("SALE"))
                  || (!contact.getEmail().equals("") && this.crawlingDao.contactEmailCount(contact.getEmail()) > 5)) {
                contact.setType("SALE");
              }
            }
            if (contactTemp.isEmailExisting()) {
              this.crawlingDao.updateContact(contact);
            }
          } else {
            this.crawlingDao.addContact(contact);
          }
          item.setContactId(contact.getId());

          if (itemId > 0) {
            crawlingDao.updateItem(item);
          } else {
            crawlingDao.addItem(item);
            itemsAddedCount++;
          }
        }
      }
      crawlingTracking.setItemsCount(crawlingTracking.getItems() == null ? 0 : crawlingTracking.getItems().size());
      crawlingTracking.setItemsAdded(itemsAddedCount);
      // Saving tracking info.
      crawlingDao.addCrawlingTracking(crawlingTracking);
    }
    return crawlingTrackingMap;
  }

  /**
   * Get list of products from given Category.
   * @param pageLink
   */
  private void getTrackingAndItems(String pageLink, Map<String, CrawlingTracking> crawlingTrackingMap, boolean recrawl) {
    try {
      Document document = Jsoup.connect(pageLink).get();
      Elements titleElements = document.select("div.product-list-page").select("div.Title");
      String categoryName = titleElements.select("h1").get(0).text();

      String source = pageLink;
      source = source.substring(source.indexOf("//") + 2);

      CrawlingTracking crawlingTracking = crawlingTrackingMap.get(pageLink);
      if (null == crawlingTracking) {
        crawlingTracking = new CrawlingTracking();
        crawlingTracking.setName(categoryName);
        crawlingTracking.setUrl(pageLink);
        crawlingTracking.setSource(source);
        crawlingTrackingMap.put(pageLink, crawlingTracking);
      }

      String currentPage = pageLink;
      String nextPage = null;

      Set<String> urls = new HashSet<>();

      do {
        LOGGER.info(">>> Get links on page: " + currentPage);
        try {
          Thread.sleep(1800);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
        document = Jsoup.connect(currentPage).get();
        Elements hrefTagsPager = document.select("div.background-pager-right-controls").get(0).children();

        Elements elements = document.select("div.Main");
        Elements itemsList = elements.get(0).select("div.search-productItem");
        for (Element element : itemsList) {
          Elements titleOfProduct = element.select("div.p-title");
          String link = titleOfProduct.get(0).select("a").attr("abs:href");
          urls.add(link);
        }

        for (int i = 0; i < hrefTagsPager.size(); i++) {
          Element href = hrefTagsPager.get(i);
          String page = href.attr("abs:href");
          nextPage = null; // for the last page
          if (page.length() == currentPage.length() && page.lastIndexOf(currentPage) == 0) {
            nextPage = hrefTagsPager.get(i + 1).attr("abs:href");
            currentPage = nextPage;
            break;
          }
        }

      } while (nextPage != null && nextPage.length() > 0);

      Iterator<String> iterator = null;
      if (!recrawl) {
        iterator = urls.iterator();
        while (iterator.hasNext()) {
          String link = iterator.next();
          if (this.crawlingDao.isItemExisting(link) > 0) {
            iterator.remove();
          }
        }
      }

      iterator = urls.iterator();
      int i = 0;
      while (iterator.hasNext()) {
        String link = iterator.next();
        LOGGER.info("post " + ++i + " / " + urls.size() + ", " + link);
        try {
          Thread.sleep(2000);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
        getItemDetails(link, crawlingTracking);
      }

    } catch (HttpStatusException e) {
      System.err.println("For '" + pageLink + "': " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("For '" + pageLink + "': " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void getItemDetails(String itemLink, CrawlingTracking crawlingTracking) {
    int itemCrawled = crawlingTracking.getItemsCrawled();
    Set<Item> items = crawlingTracking.getItems();
    if (null == items) {
      items = new HashSet<>();
    }
    try {
      Document document = Jsoup.connect(itemLink).get();
      Date start = new Date();

      String description = document.select("div.pm-desc").get(0).text();
      String title = document.select("div.pm-title").get(0).select("h1").get(0).text();

      Element productHeaderEl = document.select("div.kqchitiet").get(0);

      String typeAndPropertyTypeOriginal = productHeaderEl.select("span.diadiem-title.mar-right-15").get(0).select("a").get(0).text();
      typeAndPropertyTypeOriginal = StringUtils.stripAccents(typeAndPropertyTypeOriginal);
      String typeAndPropertyType = typeAndPropertyTypeOriginal.substring(0, typeAndPropertyTypeOriginal.indexOf("tai"));
      String businessType = typeAndPropertyType.split(" ")[0].trim();
      String propertyType = typeAndPropertyType.substring(typeAndPropertyTypeOriginal.indexOf(businessType) + businessType.length()).trim();

      businessType = businessService.getBusinessType(businessType);

      propertyType = businessService.getPropertyType(propertyType);

      String district = null;
      String city = null;
      String location = productHeaderEl.select("span.diadiem-title.mar-right-15").get(0).textNodes().get(2).text();
      String[] locationArray = location.split("-");
      if (locationArray.length == 2) {
        city = StringUtils.stripAccents(locationArray[1].trim());
        if (typeAndPropertyTypeOriginal.indexOf("tai") > 0) { // sometimes district is not here (at position 2)
          typeAndPropertyTypeOriginal = typeAndPropertyTypeOriginal.substring(typeAndPropertyTypeOriginal.indexOf("tai") + 4).trim();
          if (typeAndPropertyTypeOriginal.contains("Quan")) {
            typeAndPropertyTypeOriginal = typeAndPropertyTypeOriginal.replace("Quan", "").trim();
          }
          district = typeAndPropertyTypeOriginal;
        }
      } else if (locationArray.length == 3) {
        district = StringUtils.stripAccents(locationArray[1].trim());
        if (district.contains("Quan")) {
          district = district.replace("Quan", "").trim();
        }
        city = StringUtils.stripAccents(locationArray[2].trim());
      }

      String acreage = productHeaderEl.select("span.gia-title:not(.mar-right-15)").select("strong").get(0).text();
      String[] acreageArray = acreage.split("m");
      BigDecimal acreageInBigDecimal = null;
      if (acreageArray.length == 2) {
        acreage = acreageArray[0];
        acreageInBigDecimal = new BigDecimal(acreage);
      }

      String price = productHeaderEl.select("span.gia-title.mar-right-15").select("strong").get(0).text();
      BigDecimal priceInBigDecimal = null;
      String[] priceArray = price.split(" ");
      if (priceArray.length == 2) {
        price = priceArray[0];
        String suffix = priceArray[1];
        suffix = StringUtils.stripAccents(suffix);
        if (suffix.equals("ty ")) {
          priceInBigDecimal = new BigDecimal(price).multiply(new BigDecimal(1000));
        } else if (suffix.equals("trieu ")) {
          priceInBigDecimal = new BigDecimal(price);
        } else  if (suffix.equals("trieu/m² ")) {
          if (null != acreageInBigDecimal) {
            priceInBigDecimal = new BigDecimal(price).multiply(acreageInBigDecimal);
          }
        }
        if (null != priceInBigDecimal && priceInBigDecimal.compareTo(new BigDecimal("999999999"))  > 0) {
          priceInBigDecimal = null;
        }
      }

      Element itemDescription = document.select("div.div-table").get(0);
      String type = itemDescription.select("div.table-detail").get(0).select("div.row").get(0).select("div.right").get(0).text();
      String address = itemDescription.select("div.table-detail").get(0).select("div.row").get(1).select("div.right").get(0).text();
      String contactName = "";
      Elements contactInfo = itemDescription.select("div.div-table-cell.table2").get(0).select("div.table-detail");
      contactName = contactInfo.select("div#LeftMainContent__productDetail_contactName").size() > 0 ? contactInfo.select("div#LeftMainContent__productDetail_contactName").get(0).select("div.right").get(0).text() : "";
//      String contactPhone = itemDescription.select("div.div-table-cell.table2").get(0).select("div.table-detail").select("div#LeftMainContent__productDetail_contactPhone").get(0).select("div.right").get(0).text();
      String contactMobile = itemDescription.select("div.div-table-cell.table2").get(0).select("div.table-detail").select("div#LeftMainContent__productDetail_contactMobile").get(0).select("div.right").get(0).text();

//      itemDescription.select("div.div-table-cell.table2").get(0).select("div.table-detail").select("div#contactEmail").get(0).select("script").first();
      Elements unprocessedEmail_elements = itemDescription.getElementsByTag("script");
      String email = "";
      if (unprocessedEmail_elements.size() > 0) {
        String unprocessedEmail = unprocessedEmail_elements.get(0).dataNodes().get(0).getWholeData();
        unprocessedEmail = unprocessedEmail.substring(unprocessedEmail.indexOf("mailto:") + 7, unprocessedEmail.lastIndexOf("'>&#"));
        String[] characters = unprocessedEmail.split(";");
        email = getEmailFromCharaters(characters);
      }

      Element moreInfo = document.select("div.prd-more-info").get(0);
      String publishDate = moreInfo.select("div").get(4).text();
      publishDate = publishDate.substring(publishDate.lastIndexOf(": ") + 2);
      String endDate = moreInfo.select("div").get(5).text();
      endDate = endDate.substring(endDate.lastIndexOf(": ") + 2);

      Date end = new Date();

      SimpleDateFormat spd = new SimpleDateFormat("dd-MM-yyyy");
      Item item = new Item();
      item.setTitle(title);
      item.setDescription(description);
      item.setAddress(address);
      item.setContactName(contactName);
      item.setContactNumber(
          contactMobile != null ? contactMobile.replace(" ", "").replace(".", "") : null
      );
      item.setContactEmail(email.toLowerCase());
      item.setPublishDate(spd.parse(publishDate));
      item.setEndDate(spd.parse(endDate));
      item.setPrice(priceInBigDecimal);
      item.setAcreage(acreageInBigDecimal);
      item.setSource(crawlingTracking.getSource());
      item.setType(businessType);
      item.setPropertyType(propertyType);
      item.setCity(city == null ? "UNKNOWN" : city);
      item.setDistrict(district == null ? "UNKNOWN" : district);
      item.setUrl(itemLink);
      item.setCrawlingStart(start);
      item.setCrawlingEnd(end);
      item.setCrawlingTime(start.getTime() - end.getTime());
      items.add(item);

      crawlingTracking.addItems(items);
      itemCrawled++;
      crawlingTracking.setItemsCrawled(itemCrawled);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
  }
  
  private String getEmailFromCharaters(String[] charaters) {
    String email = "";
    for (String character : charaters) {
      character = character.substring(character.indexOf("$#") + 3);
      char c = (char) Integer.valueOf(character).intValue();
      email += c;
    }
    return email;
  }
}