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
import wap.api.rest.crawling.bds.beans.Item;
import wap.api.rest.crawling.bds.interfaces.ICrawlingDao;
import wap.api.rest.crawling.bds.interfaces.ICrawlingService;

import java.io.IOException;
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
  public Map<String, Category> saveCrawledData(List<String> pages) {

    Map<String, Category> categoryMap = new HashMap<>();
    for (String page : pages) {
      getCategoriesAndItems(page, categoryMap);
    }

    Set<String> keys = categoryMap.keySet();
    for (String key : keys) {
      Category category = categoryMap.get(key);

      // Saving category
      if (crawlingDao.isCategoryExisting(category.getUrl())) {
        crawlingDao.updateCategory(category);
        category.setId(crawlingDao.getCategoryId(category.getUrl()));
      } else {
        crawlingDao.addCategory(category);
      }

      Set<Item> items = category.getItems();
      for (Item item: items) {
        // Saving Product
        item.setCategoryId(category.getId());
        if (crawlingDao.isItemExisting(item.getUrl(), category.getUrl())) {
          crawlingDao.updateItem(item);
        } else {
          crawlingDao.addItem(item);
        }
      }
    }
    return categoryMap;
  }

  /**
   * Get list of products from given Category.
   * @param categoryLink
   */
  private void getCategoriesAndItems(String categoryLink, Map<String, Category> categoryMap) {
    try {
      Document document = Jsoup.connect(categoryLink).get();
      String categoryName = document.select("div.product-list-page").get(0).select("div.Title").select("h1").get(0).text();
      Category category = categoryMap.get(categoryLink);
      if (null == category) {
        category = new Category();
        category.setName(categoryName);
        category.setUrl(categoryLink);
        categoryMap.put(categoryLink, category);
      }

//      document.select("div.background-pager-right-controls").get(0);
//      document.select("div.background-pager-right-controls").get(0).child(1).attr("abs:href");
      String currentPage = categoryLink;
      String nextPage = null;
      do {
        document = Jsoup.connect(currentPage).get();
        Elements hrefTags = document.select("div.background-pager-right-controls").get(0).children();


        LOGGER.info(">>> Crawling category data: " + currentPage);
//      Category category = getCategoryDetails(categoryLink, categoryMap);

        Elements elements = document.select("div.Main");
        Elements itemsList = elements.get(0).select("div.search-productItem");
        for (Element element : itemsList) {
          Elements titleOfProduct = element.select("div.p-title");
          String link = titleOfProduct.get(0).select("a").attr("abs:href");

          getItemDetails(link, category);
        }

        if (category.getItems().size() > 220) {
          break;
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

      } while (nextPage != null);

    } catch (IOException e) {
      System.err.println("For '" + categoryLink + "': " + e.getMessage());
    }
  }

  private void getItemDetails(String itemLink, Category category) {
    Set<Item> items = category.getItems();
    if (null == items) {
      items = new HashSet<>();
    }
    try {
      Document document = Jsoup.connect(itemLink).get();
      String description = document.select("div.pm-desc").get(0).text();
      String title = document.select("div.pm-title").get(0).select("h1").get(0).text();

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

      SimpleDateFormat spd = new SimpleDateFormat("dd-MM-yyyy");
      Item item = new Item();
      item.setTitle(title);
      item.setDescription(description);
      item.setAddress(address);
      item.setContactName(contactName);
      item.setContactNumber(contactMobile);
      item.setContactEmail(email);
      item.setPublishDate(spd.parse(publishDate));
      item.setEndDate(spd.parse(endDate));
      item.setUrl(itemLink);
      items.add(item);

      category.addItems(items);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
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