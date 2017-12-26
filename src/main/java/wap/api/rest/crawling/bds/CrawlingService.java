package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.beans.Category;
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

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Autowired
  public CrawlingService(@Qualifier("bdsCrawlingDao") ICrawlingDao crawlingDao) {
    Assert.notNull(crawlingDao);

    this.crawlingDao = crawlingDao;
  }

  @Override
  public Map<String, CrawlingTracking> saveCrawledData(List<String> pages) {

    Map<String, CrawlingTracking> crawlingTrackingMap = new HashMap<>();
    Category category = new Category();
    for (String page : pages) {
      getTrackingAndItems(page, crawlingTrackingMap, category);
    }

    // Saving category.
    Long categoryId = crawlingDao.isCategoryExisting(category.getUrl());
    if (categoryId > 0) {
      category.setId(categoryId);
    } else {
      crawlingDao.addCategory(category);
    }

    Set<String> keys = crawlingTrackingMap.keySet();
    for (String key : keys) {
      CrawlingTracking crawlingTracking = crawlingTrackingMap.get(key);
      crawlingTracking.setItemsCount(crawlingTracking.getItems().size());
      // Saving tracking info.
      crawlingDao.addCrawlingTracking(crawlingTracking);

      Set<Item> items = crawlingTracking.getItems();
      if (null != items && items.size() > 0) {
        for (Item item: items) {
          // Saving Product
          item.setCategoryId(crawlingTracking.getId());
          Long itemId = crawlingDao.isItemExisting(item.getUrl());

          if (itemId > 0) {
            crawlingDao.updateItem(item);
            item.setId(itemId);
          } else {
            crawlingDao.addItem(item);
          }

          // Tracking item
          crawlingDao.trackingItem(crawlingTracking.getId(), item.getId());

          // Add relationship between category & item.
          Boolean itemLinkedToCategory = crawlingDao.isItemLinkedToCategory(item.getId(), category.getId());
          if (!itemLinkedToCategory) {
            crawlingDao.connectItemToCategory(category.getId(), item.getId());
          }
        }
      }

    }
    return crawlingTrackingMap;
  }

  /**
   * Get list of products from given Category.
   * @param pageLink
   */
  private void getTrackingAndItems(String pageLink, Map<String, CrawlingTracking> crawlingTrackingMap, Category category) {
    try {
      Document document = Jsoup.connect(pageLink).get();
      String categoryName = document.select("div.product-list-page").get(0).select("div.Title").select("h1").get(0).text();

      category.setName(categoryName);
//      category.setUrl(pageLink);
      String source = pageLink;
      source = source.substring(source.indexOf("//") + 2);
      category.setSource(source.substring(0, source.indexOf("/")));
      String name = source;
      name = name.substring(name.indexOf("/") + 1);
      name = name.substring(0, name.indexOf("/"));
//      category.setCategoryName(name);

      String categoryUrl = pageLink.substring(0, pageLink.indexOf(name) + name.length());
      category.setUrl(categoryUrl);

      CrawlingTracking crawlingTracking = crawlingTrackingMap.get(pageLink);
      if (null == crawlingTracking) {
        crawlingTracking = new CrawlingTracking();
        crawlingTracking.setName(categoryName);
        crawlingTracking.setUrl(pageLink);
        crawlingTrackingMap.put(pageLink, crawlingTracking);
      }

//      document.select("div.background-pager-right-controls").get(0);
//      document.select("div.background-pager-right-controls").get(0).child(1).attr("abs:href");
      String currentPage = pageLink;
      String nextPage = null;
      do {
        LOGGER.info(">>> Crawling on page: " + currentPage);
        document = Jsoup.connect(currentPage).get();
        Elements hrefTags = document.select("div.background-pager-right-controls").get(0).children();

        Elements elements = document.select("div.Main");
        Elements itemsList = elements.get(0).select("div.search-productItem");
        for (Element element : itemsList) {
          Elements titleOfProduct = element.select("div.p-title");
          String link = titleOfProduct.get(0).select("a").attr("abs:href");

          getItemDetails(link, crawlingTracking);
        }

        for (int i = 0; i < hrefTags.size(); i++) {
          Element href = hrefTags.get(i);
          String page = href.attr("abs:href");
          nextPage = null; // for the last page
          if (page.length() == currentPage.length() && page.lastIndexOf(currentPage) == 0) {
            nextPage = hrefTags.get(i + 1).attr("abs:href");
            currentPage = nextPage;
            break;
          }
        }

      } while (nextPage != null && nextPage.length() > 0);

    } catch (IOException e) {
      System.err.println("For '" + pageLink + "': " + e.getMessage());
    }
  }

  private void getItemDetails(String itemLink, CrawlingTracking crawlingTracking) {
    int itemCrawled = crawlingTracking.getItemsCrawled();
    Set<Item> items = crawlingTracking.getItems();
    if (null == items) {
      items = new HashSet<>();
    }
    try {
//      this.LOGGER.info(itemLink);
      Document document = Jsoup.connect(itemLink).get();
      Date start = new Date();

      String description = document.select("div.pm-desc").get(0).text();
      String title = document.select("div.pm-title").get(0).select("h1").get(0).text();
      String price = document.select("div.kqchitiet").get(0).select("span.gia-title.mar-right-15").select("strong").get(0).text();
      BigDecimal priceInBigDecimal = null;
      String[] priceArray = price.split(" ");
      if (priceArray.length == 2) {
        price = priceArray[0];
        String suffix = priceArray[1];
        if (suffix.equals("tỷ ")) {
          priceInBigDecimal = new BigDecimal(price).multiply(new BigDecimal(1000000));
        } else if (suffix.equals("triệu ")) {
          priceInBigDecimal = new BigDecimal(price).multiply(new BigDecimal(1000));
        }
      }

      String acreage = document.select("div.kqchitiet").get(0).select("span.gia-title:not(.mar-right-15)").select("strong").get(0).text();
      String[] acreageArray = acreage.split("m");
      BigDecimal acreageInBigDecimal = null;
      if (acreageArray.length == 2) {
        acreage = acreageArray[0];
        acreageInBigDecimal = new BigDecimal(acreage);
      }

      String location = document.select("div.kqchitiet").get(0).select("span.diadiem-title.mar-right-15").get(0).textNodes().get(2).text();
      String[] locationArray = location.split("-");
      String district = null;
      String city = null;
      if (locationArray.length == 2) {
        city = locationArray[1].trim();
      } else if (locationArray.length == 3) {
        district = locationArray[1].trim();
        city = locationArray[2].trim();
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
//      item.setDescription(description);
      item.setAddress(address);
      item.setContactName(contactName);
      item.setContactNumber(contactMobile);
      item.setContactEmail(email);
      item.setPublishDate(spd.parse(publishDate));
      item.setEndDate(spd.parse(endDate));
      item.setDistrict(district);
      item.setCity(city);
      item.setPrice(priceInBigDecimal);
      item.setAcreage(acreageInBigDecimal);
      item.setUrl(itemLink);
      item.setCrawlingStart(start);
      item.setCrawlingEnd(end);
      item.setCrawlingTime(start.getTime() - end.getTime());
      items.add(item);

      crawlingTracking.addItems(items);
      itemCrawled++;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    crawlingTracking.setItemsCrawled(itemCrawled);
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