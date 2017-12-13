package crwlr.api.rest.crawling.lazada;

import crwlr.api.rest.crawling.lazada.beans.Vendor;
import crwlr.api.rest.crawling.lazada.beans.VendorProduct;
import crwlr.api.rest.crawling.lazada.interfaces.ICrawlingDao;
import crwlr.api.rest.crawling.lazada.interfaces.ICrawlingService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
@Service("crawlingService")
public class CrawlingService implements ICrawlingService {

  private final ICrawlingDao crawlingDao;

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Autowired
  public CrawlingService(@Qualifier("crawlingDao") ICrawlingDao crawlingDao) {
    Assert.notNull(crawlingDao);

    this.crawlingDao = crawlingDao;
  }

  @Override
  public Map<String, Vendor> saveCrawledData(List<String> pages) {

    Map<String, Vendor> vendorMap = new HashMap<>();
    for (String page : pages) {
      getVendorProduct(page, vendorMap);
    }

    Set<String> keys = vendorMap.keySet();
    for (String key : keys) {
      Vendor vendor = vendorMap.get(key);

      // Saving vendor
      if (crawlingDao.isVendorExisting(vendor.getName())) {
        crawlingDao.updateVendor(vendor);
      } else {
        crawlingDao.addVendor(vendor);
      }

      Set<VendorProduct> products = vendor.getProducts();
      for (VendorProduct product: products) {

        // Saving Product
        // link of product contains accessing time.
//        if (crawlingDao.isProductExisting(product.getName(), vendor.getName(), product.getLink())) {
//          crawlingDao.updateVendorProduct(product, vendor.getName());
//        } else {
//          crawlingDao.addVendorProduct(product, vendor.getName());
//        }

        // Saving Product -- products can display more than 1 time.
          crawlingDao.addVendorProduct(product, vendor.getName());
      }
    }
    return vendorMap;
  }

  /**
   * Get list of products from given Vendor.
   * @param vendorLink
   */
  private void getVendorProduct(String vendorLink, Map<String, Vendor> vendorMap) {
    try {
      Document document = Jsoup.connect(vendorLink).get();


      // ********** Get Vendor info.
//      String sellerId = document.select("body").attr("data-spm");
//      JSONObject jsonObject = new JSONObject(document.select("div.c-header-search").attr("data-js-component-params").toString());
//      jsonObject.getJSONObject("searchContext");
//      String sellerId = jsonObject.getJSONObject("searchContext").get("EntityID").toString();


      String sellerKey = vendorLink.substring(vendorLink.lastIndexOf("/") + 1);

      LOGGER.info(">>> Crawling vendor data: " + vendorLink);
      Vendor vendor = getVendorDetails(sellerKey, vendorLink, vendorMap);

      // ********** Get list of products info
      Elements content = document.select(".c-product-list");

      // Vendor like "value-market" has ".c-product-list", but the other like the-bro-store uses REST API to get products list.
      if (content.size() != 0) {
        //document.select("div.c-paging").select("div.c-paging__wrapper").select("a.c-paging__link:not(.c-paging__link-current)") // get links of pages 2, 3 and so on
        //document.select("div.c-paging").select("div.c-paging__wrapper").select("a.c-paging__link") // get links of pages 1, 2, 3 and so on
        this.readVendorContent(content, vendor, vendorMap);

        //in case Vendor has more pages (from 2nd page)
        Elements pages = document.select("div.c-paging").select("div.c-paging__wrapper").select("a.c-paging__link:not(.c-paging__link-current)");
        for (Element page : pages) {
          Document nextPage = Jsoup.connect(page.attr("abs:href")).get();
          Elements contentOfNextPage = nextPage.select(".c-product-list");

          this.readVendorContent(contentOfNextPage, vendor, vendorMap);
        }

      } else {
        JSONObject json = new JSONObject(
                IOUtils.toString(new URL(String.format("https://catalog-rendering-api.lazada.sg/v1/seller/catalog?sort=popularity&offset=0&platform=desktop&view_type=gridView&with_filters=1&seller_key=%s&lang=en&limit=100",sellerKey)),
                        Charset.forName("UTF-8")));
        JSONObject catalog = json.getJSONObject("catalog");
        JSONArray items = catalog.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
          JSONObject item = items.getJSONObject(i);
          JSONArray productsInRow = item.getJSONArray("items");
          for (int j = 0; j < productsInRow.length(); j++) {
            JSONObject product = productsInRow.getJSONObject(j);
            String productLink = product.getJSONObject("settings").getString("productLink");
            if (null == vendor) {
              getProductDetails(productLink, vendorMap);
            } else {
              getProductDetails(productLink, vendor);
            }
          }
        }

      }

    } catch (IOException e) {
      System.err.println("For '" + vendorLink + "': " + e.getMessage());
    }
    catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void readVendorContent(Elements content, Vendor vendor, Map<String, Vendor> vendorMap) {
    Elements productLinks = content.select("a[href].c-product-card__img-placeholder-inner");  // current page

    for (Element link : productLinks) {
      String productLink = link.attr("abs:href");
      if (null == vendor) {
        getProductDetails(productLink, vendorMap);
      } else {
        getProductDetails(productLink, vendor);
      }
    }
  }

  /**
   *
   * @param sellerKey
   * @param vendorLink
   * @param vendorMap
   * @return
   */
  private Vendor getVendorDetails(String sellerKey, String vendorLink, Map<String, Vendor> vendorMap) {
    Vendor vendor = null;
    if (!StringUtils.isEmpty(sellerKey)) {
//      sellerKey = sellerKey.substring(sellerKey.indexOf("-") + 1);
      try {
        // get vendor info by seller_id, sometimes getting error because some vendors do not have ID.
//        JSONObject json = new JSONObject(
//            IOUtils.toString(new URL("https://seller-transparency-api.lazada.sg/v1/seller/transparency?platform=desktop&lang=en&seller_id=" + sellerId),
//                Charset.forName("UTF-8")));

        JSONObject json = new JSONObject(
        IOUtils.toString(new URL("https://seller-transparency-api.lazada.sg/v1/seller/transparency?platform=desktop&lang=en&seller_key=" + sellerKey),
        Charset.forName("UTF-8")));
        JSONObject seller = (JSONObject) json.get("seller");
        String name = seller.getString("name");
        vendor = vendorMap.get(name);
        if (null == vendor) {
          vendor = new Vendor();
          vendorMap.put(name, vendor);
        }
        String location = seller.getString("location");
        String shipOnTime = seller.getJSONObject("shipped_on_time").getString("average_rate");
        String positive = seller.getJSONObject("seller_reviews").getJSONObject("positive").getString("total");
        String negative = seller.getJSONObject("seller_reviews").getJSONObject("negative").getString("total");
        String neutral = seller.getJSONObject("seller_reviews").getJSONObject("neutral").getString("total");
        String timeOnLazada = seller.getJSONObject("time_on_lazada").getString("months");
        String mainCategory = seller.getJSONObject("category").getString("name");
        String sellerSize = seller.getString("size");
        String rating = seller.getString("rate");

        vendor.setName(name);
        vendor.setLocation(location);
        vendor.setShipOnTime(Double.valueOf(shipOnTime));
        vendor.setPositive(StringUtils.isEmpty(positive) ? null : Integer.valueOf(positive));
        vendor.setNegative(StringUtils.isEmpty(negative) ? null : Integer.valueOf(negative));
        vendor.setNeutral(StringUtils.isEmpty(neutral) ? null : Integer.valueOf(neutral));
        vendor.setTimeOnLazada(StringUtils.isEmpty(timeOnLazada) ? null : Integer.valueOf(timeOnLazada));
        vendor.setSize(StringUtils.isEmpty(sellerSize) ? null : Integer.valueOf(sellerSize));
        vendor.setRating(StringUtils.isEmpty(rating) ? null : Double.valueOf(rating));
        vendor.setLink(vendorLink);
        vendor.setMainCategory(mainCategory);
      } catch (JSONException | IOException e) {
        e.printStackTrace();
        return null;
      }
    }
    return vendor;
  }

  private void getProductDetails(String productLink, Map<String, Vendor> vendorMap) {
    try {
      Document document = Jsoup.connect(productLink).get();


      String vendorName = document.select(".basic-info__name").get(0).text();

      Vendor vendor = vendorMap.get(vendorName);
      if (null == vendor) {
        vendor = new Vendor();
        vendor.setName(vendorName);

        String rating = document.select("div.seller-rating").attr("data-tooltip-header");
        rating = rating.substring(0, rating.indexOf("/"));
        vendor.setRating(StringUtils.isEmpty(rating) ? null : Double.valueOf(rating));

        Elements timeOnLazada = document.select(".time-on-lazada__value");
        vendor.setTimeOnLazada(timeOnLazada.size() > 0 ? Integer.valueOf(timeOnLazada.get(0).text()) : null);

        String size = document.select(".seller-size__content").select(".seller-size-icon").attr("data-level");
        vendor.setSize(StringUtils.isEmpty(size) ? null : Integer.valueOf(size));

        vendorMap.put(vendorName, vendor);
      }

      VendorProduct vendorProduct = new VendorProduct();
      String productName = document.select("#prod_title").text();
      Elements categories = document.select(".breadcrumb__list").select(".breadcrumb__item-text").select("a[title]");
      String category = null;
      if (null != categories && categories.size() > 0) {
        category = categories.get(0).select("span").text();
      }

      vendorProduct.setName(productName);
      vendorProduct.setCategory(category);
      vendorProduct.setLink(productLink);

      Set<VendorProduct> products = vendor.getProducts();
      if (null == products) {
        products = new HashSet<>();
        vendor.setProducts(products);
      }
      products.add(vendorProduct);

    } catch (IOException e) {
      System.err.println("For '" + productLink + "': " + e.getMessage());
    }
  }

  private void getProductDetails(String productLink, Vendor vendor) {
    try {
      Document document = Jsoup.connect(productLink).get();

      VendorProduct vendorProduct = new VendorProduct();
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

      vendorProduct.setName(productName);
      vendorProduct.setCategory(category);
      vendorProduct.setLink(productLink);
      vendorProduct.setPrice(new BigDecimal(originalPriceStr));
      vendorProduct.setDiscountPercent(discountPercent);
      vendorProduct.setDiscountPrice(new BigDecimal(priceStr));
      vendorProduct.setCurrency(currency);
      vendorProduct.setImageURL(imageURL);

      Set<VendorProduct> products = vendor.getProducts();
      if (null == products) {
        products = new HashSet<>();
        vendor.setProducts(products);
      }
      if (products.contains(vendorProduct)) {
        LOGGER.info(vendorProduct.getName() + " is already stored");
      }
      products.add(vendorProduct);

    } catch (IOException e) {
      System.err.println("For '" + productLink + "': " + e.getMessage());
    }
  }
}