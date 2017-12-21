package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.beans.Category;
import wap.api.rest.crawling.bds.beans.Item;
import wap.api.rest.crawling.bds.interfaces.ICrawlingDao;
import wap.common.dao.DaoUtils;

import java.util.Date;

/**
 * Date: 12/21/2017
 * The DAO is responsible for saving (ADD & UPDATE) crawled data from given pages.
 * @author haho
 */
@Repository("bdsCrawlingDao")
public class CrawlingDao implements ICrawlingDao {

  private static final Logger LOGGER = LogManager.getLogger(CrawlingDao.class);

  private final NamedParameterJdbcTemplate namedTemplate;

  @Autowired
  public CrawlingDao(NamedParameterJdbcTemplate namedTemplate) {
    Assert.notNull(namedTemplate);

    this.namedTemplate = namedTemplate;
  }

  @Override
  public boolean isVendorExisting(String name) {
    final String sql =
        "SELECT COUNT(*) from crwlr_vendors WHERE name = :name"
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", name);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) > 0;
  }

  @Override
  public void addVendor(Category category) {
    final String sql =
        "INSERT INTO crwlr_vendors (name, location, positive, neutral, negative, link, timeOnLazada, rating, size, mainCategory, shipOnTime)"
      + "VALUE (:name, :location, :positive, :neutral, :negative, :link, :timeOnLazada, :rating, :size, :mainCategory, :shipOnTime)         "
        ;

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", category.getName());
    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public void updateVendor(Category category) {
    final String sql =
      " UPDATE crwlr_vendors                                                                                         "
    + "   SET location   = :location, positive = :positive, neutral = :neutral, negative = :negative, link = :link,  "
    + "    timeOnLazada = :timeOnLazada, rating = :rating, size = :size, mainCategory = :mainCategory,               "
    + "    shipOnTime = :shipOnTime, updated = :updated                                                              "
    + " WHERE name = :name                                                                                           "
    ;

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", category.getName());
    paramsMap.addValue("updated", new Date());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public boolean isProductExisting(String name, String vendorName, String link) {
    final String sql =
        "SELECT COUNT(*) from crwlr_products WHERE name = :name AND vendor_name = :vendor_name AND link = :link"
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", name);
    paramsMap.addValue("vendor_name", vendorName);
    paramsMap.addValue("link", link);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) > 0;
  }

  @Override
  public void addVendorProduct(Item product, String vendorName) {
    final String sql =
              "INSERT INTO crwlr_products (name, category, vendor_name, link, price, discountPrice, currency, discountPercent, imageURL)"
            + "  VALUE (:name, :category, :vendor_name, :link, :price, :discountPrice, :currency, :discountPercent, :imageURL)           "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", product.getTitle());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public void updateVendorProduct(Item product, String vendorName) {
    final String sql =
        "UPDATE crwlr_products SET category = :category, updated = :updated, link = :link, imageURL = :imageURL,   "
      + " price = :price, discountPrice = :discountPrice, currency = :currency, discountPercent = :discountPercent "
      + " WHERE name = :name AND vendor_name = :vendor_name                                                        "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", product.getTitle());
    paramsMap.addValue("vendor_name", vendorName);
    paramsMap.addValue("updated", new Date());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }
}
