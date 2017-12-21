package wap.api.rest.crawling.bds;

import org.apache.commons.lang3.StringUtils;
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
      if (crawlingDao.isVendorExisting(category.getName())) {
        crawlingDao.updateVendor(category);
      } else {
        crawlingDao.addVendor(category);
      }

      Set<Item> products = category.getProducts();
      for (Item product: products) {

        // Saving Product
        // link of product contains accessing time.
//        if (crawlingDao.isProductExisting(product.getName(), category.getName(), product.getLink())) {
//          crawlingDao.updateVendorProduct(product, category.getName());
//        } else {
//          crawlingDao.addVendorProduct(product, category.getName());
//        }

        // Saving Product -- products can display more than 1 time.
          crawlingDao.addVendorProduct(product, category.getName());
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

      LOGGER.info(">>> Crawling category data: " + categoryLink);
//      Category category = getCategoryDetails(categoryLink, categoryMap);

      Elements elements = document.select("div.Main");
      Elements itemsList = elements.get(0).select("div.search-productItem");
      List<String> itemsLinks = new ArrayList<>();
      for (Element element : itemsList) {
        Elements titleOfProduct = element.select("div.p-title");
        String link = titleOfProduct.get(0).select("a").attr("abs:href");
        itemsLinks.add(link);
      }


      // ********** Get list of items info
      Elements content = document.select(".c-product-list");

      // Category like "value-market" has ".c-product-list", but the other like the-bro-store uses REST API to get products list.
//      if (content.size() != 0) {
//        //document.select("div.c-paging").select("div.c-paging__wrapper").select("a.c-paging__link:not(.c-paging__link-current)") // get links of pages 2, 3 and so on
//        //document.select("div.c-paging").select("div.c-paging__wrapper").select("a.c-paging__link") // get links of pages 1, 2, 3 and so on
//        this.readVendorContent(content, category, categoryMap);
//
//        //in case Category has more pages (from 2nd page)
//        Elements pages = document.select("div.c-paging").select("div.c-paging__wrapper").select("a.c-paging__link:not(.c-paging__link-current)");
//        for (Element page : pages) {
//          Document nextPage = Jsoup.connect(page.attr("abs:href")).get();
//          Elements contentOfNextPage = nextPage.select(".c-product-list");
//
//          this.readVendorContent(contentOfNextPage, category, categoryMap);
//        }
//
//      } else {
//        JSONObject json = new JSONObject(
//                IOUtils.toString(new URL(String.format("https://catalog-rendering-api.lazada.sg/v1/seller/catalog?sort=popularity&offset=0&platform=desktop&view_type=gridView&with_filters=1&seller_key=%s&lang=en&limit=100",sellerKey)),
//                        Charset.forName("UTF-8")));
//        JSONObject catalog = json.getJSONObject("catalog");
//        JSONArray items = catalog.getJSONArray("items");
//        for (int i = 0; i < items.length(); i++) {
//          JSONObject item = items.getJSONObject(i);
//          JSONArray productsInRow = item.getJSONArray("items");
//          for (int j = 0; j < productsInRow.length(); j++) {
//            JSONObject product = productsInRow.getJSONObject(j);
//            String productLink = product.getJSONObject("settings").getString("productLink");
//            if (null == category) {
//              getProductDetails(productLink, categoryMap);
//            } else {
//              getProductDetails(productLink, category);
//            }
//          }
//        }
//
//      }

    } catch (IOException e) {
      System.err.println("For '" + categoryLink + "': " + e.getMessage());
    }
  }

  private void readVendorContent(Elements content, Category category, Map<String, Category> vendorMap) {
    Elements productLinks = content.select("a[href].c-product-card__img-placeholder-inner");  // current page

    for (Element link : productLinks) {
      String productLink = link.attr("abs:href");
      if (null == category) {
        getProductDetails(productLink, vendorMap);
      } else {
        getProductDetails(productLink, category);
      }
    }
  }

  private void getProductDetails(String productLink, Map<String, Category> vendorMap) {
    try {
      Document document = Jsoup.connect(productLink).get();

      String vendorName = document.select(".basic-info__name").get(0).text();

      Category vendor = vendorMap.get(vendorName);
      if (null == vendor) {
        vendor = new Category();
        vendor.setName(vendorName);

        String rating = document.select("div.seller-rating").attr("data-tooltip-header");
        rating = rating.substring(0, rating.indexOf("/"));
//        vendor.setRating(StringUtils.isEmpty(rating) ? null : Double.valueOf(rating));

        Elements timeOnLazada = document.select(".time-on-lazada__value");
//        vendor.setTimeOnLazada(timeOnLazada.size() > 0 ? Integer.valueOf(timeOnLazada.get(0).text()) : null);

        String size = document.select(".seller-size__content").select(".seller-size-icon").attr("data-level");
//        vendor.setSize(StringUtils.isEmpty(size) ? null : Integer.valueOf(size));

        vendorMap.put(vendorName, vendor);
      }

      Item item = new Item();
      String productName = document.select("#prod_title").text();
      Elements categories = document.select(".breadcrumb__list").select(".breadcrumb__item-text").select("a[title]");
      String category = null;
      if (null != categories && categories.size() > 0) {
        category = categories.get(0).select("span").text();
      }

      item.setName(productName);
//      item.setCategory(category);
//      item.setLink(productLink);

      Set<Item> products = vendor.getProducts();
      if (null == products) {
        products = new HashSet<>();
        vendor.setProducts(products);
      }
      products.add(item);

    } catch (IOException e) {
      System.err.println("For '" + productLink + "': " + e.getMessage());
    }
  }

  private void getProductDetails(String productLink, Category vendor) {
    try {
      Document document = Jsoup.connect(productLink).get();

      Item item = new Item();
      String productName = document.select("#prod_title").text();
      Elements categories = document.select(".breadcrumb__list").select(".breadcrumb__item-text").select("a[title]");
      String category = null;
      if (null != categories && categories.size() > 0) {
        category = categories.get(0).select("span").text();
      }

      Elements currencyElements = document.select("div.prod_pricebox_price_final").select("span#special_currency_box");
      String currency = currencyElements.size() > 0 ? currencyElements.get(0).text() : "";

      Elements priceElements = document.select("span#special_price_box");
      String priceStr = priceElements.size() > 0 ? priceElements.get(0).text() : "";
      priceStr = priceStr.replace(",", "");

      Elements originalPriceElements = document.select("span.price_erase").select("span#price_box");
      String originalPriceStr = originalPriceElements.size() > 0 ? originalPriceElements.get(0).text() : "";
      originalPriceStr = originalPriceStr.substring(originalPriceStr.lastIndexOf(currency) + currency.length() + 1, originalPriceStr.length() - 1);
      originalPriceStr = originalPriceStr.replace(",", "");

      Elements discountPercentElements = document.select("div.prod_saving").select("span#product_saving_percentage");
      String discountPercentStr = discountPercentElements.size() > 0 ? discountPercentElements.get(0).text() : "";
      Double discountPercent = StringUtils.isEmpty(discountPercentStr) ? null : new Double(discountPercentStr.substring(0, discountPercentStr.length() - 1));

      String imageURL = document.select("div#productZoom").attr("data-zoom-image").toString();

      item.setName(productName);

      Set<Item> products = vendor.getProducts();
      if (null == products) {
        products = new HashSet<>();
        vendor.setProducts(products);
      }
      if (products.contains(item)) {
        LOGGER.info(item.getName() + " is already stored");
      }
      products.add(item);

    } catch (IOException e) {
      System.err.println("For '" + productLink + "': " + e.getMessage());
    }
  }
}