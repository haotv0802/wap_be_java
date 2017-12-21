package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.beans.Category;
import wap.api.rest.crawling.bds.beans.Item;
import wap.api.rest.crawling.bds.interfaces.ICrawlingDao;
import wap.common.JdbcUtils;
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
  public boolean isCategoryExisting(String url) {
    final String sql =
        "SELECT COUNT(*) FROM crwlr_categories where url = :url";
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("url", url);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) > 0;
  }

  @Override
  public void addCategory(Category category) {
    final String sql =
             "INSERT INTO crwlr_categories (name, url)"
           + "VALUE (:name, :url)                     "
        ;

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", category.getName());
    paramsMap.addValue("url", category.getUrl());
    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedTemplate.update(sql, paramsMap, keyHolder);
    final Long id = keyHolder.getKey().longValue();
    category.setId(id);
  }

  @Override
  public void updateCategory(Category category) {
    final String sql =
      "UPDATE crwlr_categories                  "
    + "   SET name  = :name, updated = :updated "
    + " WHERE url = :url                        "
    ;

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", category.getName());
    paramsMap.addValue("url", category.getUrl());
    paramsMap.addValue("updated", new Date());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public boolean isItemExisting(String url, String categoryUrl) {
    final String sql =
    "SELECT COUNT(*) FROM crwlr_items i inner join crwlr_categories c on i.category_id = c.id where c.url = :url and i.url = :categoryUrl";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("url", url);
    paramsMap.addValue("categoryUrl", categoryUrl);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) > 0;
  }

  @Override
  public void addItem(Item item, String vendorName) {
    final String sql =
    "INSERT INTO crwlr_items (name, address, description, contactName, contactNumber, contactEmail, publish_date, end_date, url, category_id)"
  + " VALUE (:name, :address, :description, :contactName, :contactNumber, :contactEmail, :publishDate, :endDate, :url, :category_id)          ";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", item.getTitle());
    paramsMap.addValue("address", item.getAddress());
    paramsMap.addValue("description", item.getDescription());
    paramsMap.addValue("contactName", item.getContactName());
    paramsMap.addValue("contactNumber", item.getContactNumber());
    paramsMap.addValue("contactEmail", item.getContactEmail());
    paramsMap.addValue("publishDate", JdbcUtils.toSQLDate(item.getPublishDate()));
    paramsMap.addValue("endDate", JdbcUtils.toSQLDate(item.getEndDate()));
    paramsMap.addValue("url", item.getUrl());
    paramsMap.addValue("category_id", item.getCategoryId());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public void updateItem(Item product, String vendorName) {
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
