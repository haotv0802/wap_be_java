package wap.api.rest.crawling.bds;

import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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

import java.util.Calendar;
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
  public Long isCategoryExisting(String url) {
    final String sql =
        "SELECT id FROM crwlr_categories where url = :url";
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("url", url);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());
    try {
      return namedTemplate.queryForObject(sql, paramsMap, Long.class);
    } catch (EmptyResultDataAccessException ex) {
      return new Long(-1);
    }
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
  public Long isItemExisting(String url, String categoryUrl) {
    final String sql =
    "SELECT i.id FROM crwlr_items i inner join crwlr_categories c on i.category_id = c.id where i.url = :url and c.url = :categoryUrl";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("url", url);
    paramsMap.addValue("categoryUrl", categoryUrl);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());
    try {
      return namedTemplate.queryForObject(sql, paramsMap, Long.class);
    } catch (EmptyResultDataAccessException ex) {
      return new Long(-1);
    }
  }

  @Override
  public void addItem(Item item) {
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
  public void updateItem(Item item) {
    final String sql =
        "UPDATE crwlr_items SET name = :name, description = :description, address = :address, contactNumber = :contactNumber,             "
      + " contactEmail = :contactEmail, publish_date = :publish_date, end_date = :end_date, category_id = :category_id, updated = :updated"
      + " WHERE url = :url AND category_id = :category_id                                                                                 "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", item.getTitle());
    paramsMap.addValue("address", item.getAddress());
    paramsMap.addValue("description", item.getDescription());
    paramsMap.addValue("contactName", item.getContactName());
    paramsMap.addValue("contactNumber", item.getContactNumber());
    paramsMap.addValue("contactEmail", item.getContactEmail());
    paramsMap.addValue("publish_date", JdbcUtils.toSQLDate(item.getPublishDate()));
    paramsMap.addValue("end_date", JdbcUtils.toSQLDate(item.getEndDate()));
    paramsMap.addValue("url", item.getUrl());
    paramsMap.addValue("category_id", item.getCategoryId());
    paramsMap.addValue("updated", new Date());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }
}
