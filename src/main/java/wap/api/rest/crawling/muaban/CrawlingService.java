package wap.api.rest.crawling.muaban;

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
 * Date: 12/28/2017
 *
 * @author haho
 */
@Service("muabanCrawlingService")
public class CrawlingService implements ICrawlingService {

  private final ICrawlingDao crawlingDao;

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Autowired
  public CrawlingService(@Qualifier("muabanCrawlingDao") ICrawlingDao crawlingDao) {
    Assert.notNull(crawlingDao);

    this.crawlingDao = crawlingDao;
  }

  @Override
  public Map<String, CrawlingTracking> saveCrawledData(List<String> pages) {

    Map<String, CrawlingTracking> crawlingTrackingMap = new HashMap<>();
    for (String page : pages) {
      getTrackingAndItems(page, crawlingTrackingMap);
    }

    Set<String> keys = crawlingTrackingMap.keySet();
    for (String key : keys) {
      CrawlingTracking crawlingTracking = crawlingTrackingMap.get(key);
      int itemsAddedCount = 0;
      Set<Item> items = crawlingTracking.getItems();
      if (null != items && items.size() > 0) {
        for (Item item: items) {
          // Saving Product
          Long itemId = crawlingDao.isItemExisting(item.getUrl());

          if (itemId > 0) {
            crawlingDao.updateItem(item);
            item.setId(itemId);
          } else {
            crawlingDao.addItem(item);
            itemsAddedCount++;
          }

          // Saving category.
          Category category = crawlingTracking.getCategory();
          Long categoryId = crawlingDao.isCategoryExisting(category.getUrl());
          if (categoryId > 0) {
            category.setId(categoryId);
          } else {
            crawlingDao.addCategory(category);
          }

          // Add relationship between category & item.
          Boolean itemLinkedToCategory = crawlingDao.isItemLinkedToCategory(item.getId(), category.getId());
          if (!itemLinkedToCategory) {
            crawlingDao.connectItemToCategory(category.getId(), item.getId());
          }
        }

        crawlingTracking.setItemsCount(crawlingTracking.getItems().size());
        crawlingTracking.setItemsAdded(itemsAddedCount);
        // Saving tracking info.
        crawlingDao.addCrawlingTracking(crawlingTracking);
      }

    }
    return crawlingTrackingMap;
  }

  /**
   * Get list of products from given Category.
   * @param pageLink
   */
  private void getTrackingAndItems(String pageLink, Map<String, CrawlingTracking> crawlingTrackingMap) {
    try {
      Document document = Jsoup.connect(pageLink).get();
      String categoryName = document.select("div.mbn-box-list").get(0).select("div.list-title").get(0).select("h1").text();

      Category category = new Category();
      category.setName(categoryName);
//      category.setUrl(pageLink);
      String source = pageLink;
      source = source.substring(source.indexOf("//") + 2);
      category.setSource(source.substring(0, source.indexOf("/")));
      String name = source;
      name = name.substring(name.indexOf("/") + 1);
      if (name.indexOf("/") > 0) {
        name = name.substring(0, name.indexOf("/"));
      }
      category.setType("Buying");
      category.setPropertyType("House");

      String categoryUrl = pageLink.substring(0, pageLink.indexOf(name) + name.length());
      category.setUrl(categoryUrl);

      CrawlingTracking crawlingTracking = crawlingTrackingMap.get(pageLink);
      if (null == crawlingTracking) {
        crawlingTracking = new CrawlingTracking();
        crawlingTracking.setName(categoryName);
        crawlingTracking.setUrl(pageLink);
        crawlingTracking.setCategory(category);
        crawlingTrackingMap.put(pageLink, crawlingTracking);
      }

      String currentPage = pageLink;
      String nextPage = null;
      do {
        LOGGER.info(">>> Crawling on page: " + currentPage);
        document = Jsoup.connect(currentPage).get();
        Elements hrefTagsPager = document.select("div.paging").get(0).select("ul").get(0).select("li");

        Elements elements = document.select("div.Main");
        Elements itemsList = document.select("div.mbn-box-left").get(0).select("div.mbn-box-list-content");
        for (Element element : itemsList) {
          String link = element.select("a").get(0).attr("abs:href");

          getItemDetails(link, crawlingTracking);
        }

        for (int i = 0; i < hrefTagsPager.size(); i++) {
          Element href = hrefTagsPager.get(i);
          String page = href.select("li").select("a").attr("abs:href");
          nextPage = null; // for the last page
          if (page.length() == currentPage.length() && page.lastIndexOf(currentPage) == 0) {
            nextPage = hrefTagsPager.get(i + 1).attr("abs:href");
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

      String description = document.select("div.ct-body.overflow.clearfix").get(0).text();
      String title = document.select("div.cl-title.clearfix").get(0).text();

      String contactName = "";
      String contactMobile = "";
      String address = "";
      Elements contactsElements = document.select("div.ct-contact.clearfix");
      if (null != contactsElements && contactsElements.size() > 0) {
        Elements contactNameEl = contactsElements.get(0).select("div.clearfix").select("div.contact-name");
        if (null != contactNameEl && contactNameEl.size() > 0) {
          contactName = contactNameEl.get(0).text();
        }
        Elements contactMobileEl = contactsElements.get(0).select("div.clearfix").select("div.contact-mobile");
        if (null != contactMobileEl && contactMobileEl.size() > 0) {
          contactMobile = contactMobileEl.get(0).text();
        }
        if (null != contactNameEl && contactNameEl.size() > 1) {
          address = contactNameEl.get(1).text();
        }
      }

      String publishDate = "";
      String city = "";
      String district = "";
      String price = "";
      BigDecimal priceInBigDecimal = null;
      String location = "";
          Elements afterTittleEls = document.select("div.cl-price-sm.clearfix");
      if (null != afterTittleEls && afterTittleEls.size() > 0) {
        Elements priceAndLocationEles =  afterTittleEls.get(0).select("span.visible-lg.float-left");
        if (null != priceAndLocationEles && priceAndLocationEles.size() > 0) {
          if (priceAndLocationEles.size() == 1) {
            location = priceAndLocationEles.get(0).childNode(1).toString();

          } else if (priceAndLocationEles.size() == 2) {
            price = priceAndLocationEles.get(0).text().split(" ")[1];
            price = price.replace(".", "");
            priceInBigDecimal = new BigDecimal(price).divide(new BigDecimal(1000000));
            location = priceAndLocationEles.get(1).childNode(1).toString();
          }
          location = location.substring(city.indexOf("&nbsp;") + 7).trim();
          String[] cityAndDistrict = location.split("-");
          if (null != cityAndDistrict && cityAndDistrict.length == 2) {
            district = cityAndDistrict[0].trim();
            city = cityAndDistrict[1].trim();
          } else {
            city = location.trim();
          }
        }

        publishDate = afterTittleEls.get(0).select("span.float-left:not(.visible-lg)").get(0).text();
        publishDate = publishDate.substring(publishDate.indexOf(" Ngày ") + 6).trim();
      }

      Date end = new Date();

      SimpleDateFormat spd = new SimpleDateFormat("dd/MM/yyyy");
      Item item = new Item();
      item.setTitle(title);
      item.setAddress(address);
      item.setDescription(description);
      item.setContactName(contactName);
      item.setContactNumber(contactMobile);
      item.setPublishDate(spd.parse(publishDate));
      item.setCity(city);
      item.setDistrict(district);
      item.setPrice(priceInBigDecimal);
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