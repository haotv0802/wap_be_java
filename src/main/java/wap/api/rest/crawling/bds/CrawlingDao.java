package wap.api.rest.crawling.bds;

import org.apache.commons.lang3.StringUtils;
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
import wap.api.rest.crawling.bds.beans.Contact;
import wap.api.rest.crawling.bds.beans.CrawlingTracking;
import wap.api.rest.crawling.bds.beans.Item;
import wap.api.rest.crawling.bds.interfaces.ICrawlingDao;
import wap.common.JdbcUtils;
import wap.common.dao.DaoUtils;

import java.sql.SQLException;
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
  public void addCrawlingTracking(CrawlingTracking crawlingTracking) {
    final String sql =
             "INSERT INTO crwlr_crawling_tracking (name, url, items_count, items_crawled, items_added)"
           + " VALUE (:name, :url, :items_count, :items_crawled, :items_added)                        "
        ;

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", crawlingTracking.getName());
    paramsMap.addValue("url", crawlingTracking.getUrl());
    paramsMap.addValue("items_count", crawlingTracking.getItemsCount());
    paramsMap.addValue("items_crawled", crawlingTracking.getItemsCrawled());
    paramsMap.addValue("items_added", crawlingTracking.getItemsAdded());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedTemplate.update(sql, paramsMap, keyHolder);
    final Long id = keyHolder.getKey().longValue();
    crawlingTracking.setId(id);
  }

  @Override
  public Long isLocationExisting(String district, String city) {
    final String sql =
        "SELECT id FROM crwlr_locations WHERE district = :district AND city = :city";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("district", district);
    paramsMap.addValue("city", city);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());
    try {
      return namedTemplate.queryForObject(sql, paramsMap, Long.class);
    } catch (EmptyResultDataAccessException ex) {
      return new Long(-1);
    }
  }


  @Override
  public Long addLocation(String district, String city) {
    final String sql =
              "INSERT INTO crwlr_locations (district, city)"
            + " VALUE (:district, :city)                   "
        ;

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("district", district);
    paramsMap.addValue("city", city);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedTemplate.update(sql, paramsMap, keyHolder);
    return keyHolder.getKey().longValue();
  }

  @Override
  public void addContact(Contact contact) {
    final String sql =
              "INSERT INTO crwlr_contacts (name, phone, email, type, latest_item_posted_on)"
            + " VALUE (:name, :phone, :email, :type, :latest_item_posted_on)               "
        ;

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", contact.getName());
    paramsMap.addValue("phone", contact.getPhone());
    paramsMap.addValue("email", contact.getEmail());
    paramsMap.addValue("type", contact.getType());
    paramsMap.addValue("latest_item_posted_on", contact.getLatestItemPostedOn());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedTemplate.update(sql, paramsMap, keyHolder);
    Long id = keyHolder.getKey().longValue();
    contact.setId(id);
  }

  @Override
  public Long isContactExisting(String name, String phone, String email) {
    final String sql =
              "SELECT                                             "
            + "	id                                                "
            + "FROM  crwlr_contacts                               "
            + " WHERE                                             "
            + "	name = :name AND phone = :phone AND email = :email"
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", name);
    paramsMap.addValue("phone", phone);
    paramsMap.addValue("email", email);

    try {
      return namedTemplate.queryForObject(sql, paramsMap, Long.class);
    } catch (EmptyResultDataAccessException ex) {
      return new Long(-1);
    }
  }

  @Override
  public void updateContact(Contact contact) {
    final String sql =
        "UPDATE crwlr_contacts SET name = :name, phone = :phone, email = :email,"
      + " type = :type, latest_item_posted_on = :latest_item_posted_on          "
      + " WHERE id = :id                                                        "
        ;

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", contact.getName());
    paramsMap.addValue("phone", contact.getPhone());
    paramsMap.addValue("email", contact.getEmail());
    paramsMap.addValue("type", contact.getType());
    paramsMap.addValue("latest_item_posted_on", contact.getLatestItemPostedOn());
    paramsMap.addValue("id", contact.getId());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public Long isItemExisting(String url) {
    final String sql =
    "SELECT i.id FROM crwlr_posts i where i.url = :url";

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
  public Boolean isItemLinkedToCategory(Long id, Long categoryId) {
    final String sql =
          "SELECT                                           "
        + "	COUNT(*)                                        "
        + "FROM  crwlr_categories_items_details             "
        + " WHERE                                           "
        + "	category_id = :categoryId AND item_id = :itemId "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("categoryId", categoryId);
    paramsMap.addValue("itemId", id);

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) > 0;
  }

  @Override
  public void addItem(Item item) {
    final String sql =
    "INSERT INTO crwlr_posts (name, description, address, contact_name, contact_number, contact_email, publish_date,  "
  + "end_date, url, acreage, price, location_id, source, type, property_type, contact_id)                             "
  + " VALUE (:name, :description, :address, :contact_name, :contact_number, :contact_email, :publishDate,             "
  + " :endDate, :url, :acreage, :price, :locationId, :source, :type, :property_type, :contact_id)                     "
        ;

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", item.getTitle());
    paramsMap.addValue("address", item.getAddress());
    paramsMap.addValue("description", item.getDescription());
    paramsMap.addValue("contact_name", item.getContactName());
    paramsMap.addValue("contact_number", item.getContactNumber());
    paramsMap.addValue("contact_email", item.getContactEmail());
    paramsMap.addValue("publishDate", JdbcUtils.toSQLDate(item.getPublishDate()));
    paramsMap.addValue("endDate", JdbcUtils.toSQLDate(item.getEndDate()));
    paramsMap.addValue("url", item.getUrl());
    paramsMap.addValue("acreage", item.getAcreage());
    paramsMap.addValue("price", item.getPrice());
    paramsMap.addValue("locationId", item.getLocationId());
    paramsMap.addValue("source", item.getSource());
    paramsMap.addValue("source", item.getSource());
    paramsMap.addValue("type", item.getType());
    paramsMap.addValue("contact_id", item.getContactId());
    paramsMap.addValue("property_type", item.getPropertyType());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    try {
      namedTemplate.update(sql, paramsMap, keyHolder);
      final Long id = keyHolder.getKey().longValue();
      item.setId(id);
    } catch (Exception ex) {
      if (ex instanceof SQLException) {
        if (ex.getMessage().contains("Incorrect string value")) {
          item.setContactName(StringUtils.stripAccents(item.getContactName()));
          item.setTitle(StringUtils.stripAccents(item.getTitle()));
          item.setAddress(StringUtils.stripAccents(item.getAddress()));
          item.setDescription(StringUtils.stripAccents(item.getDescription()));
          namedTemplate.update(sql, paramsMap, keyHolder);
          final Long id = keyHolder.getKey().longValue();
          item.setId(id);
        }
      }
    }
  }

  @Override
  public void updateItem(Item item) {
    final String sql =
        "UPDATE crwlr_posts SET name = :name, description = :description, address = :address, contact_number = :contact_number, acreage = :acreage,"
      + " price = :price, location_id = :locationId, contact_name = :contact_name, source = :source, type = :type, property_type = :property_type, "
      + " contact_email = :contact_email, publish_date = :publish_date, end_date = :end_date, updated_at = :updated, contact_id = :contact_id      "
      + " WHERE url = :url                                                                                                                         "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", item.getTitle());
    paramsMap.addValue("address", item.getAddress());
    paramsMap.addValue("description", item.getDescription());
    paramsMap.addValue("contact_name", item.getContactName());
    paramsMap.addValue("contact_number", item.getContactNumber());
    paramsMap.addValue("contact_email", item.getContactEmail());
    paramsMap.addValue("publish_date", JdbcUtils.toSQLDate(item.getPublishDate()));
    paramsMap.addValue("end_date", JdbcUtils.toSQLDate(item.getEndDate()));
    paramsMap.addValue("url", item.getUrl());
    paramsMap.addValue("acreage", item.getAcreage());
    paramsMap.addValue("price", item.getPrice());
    paramsMap.addValue("locationId", item.getLocationId());
    paramsMap.addValue("updated", new Date());
    paramsMap.addValue("source", item.getSource());
    paramsMap.addValue("type", item.getType());
    paramsMap.addValue("contact_id", item.getContactId());
    paramsMap.addValue("property_type", item.getPropertyType());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    try {
      namedTemplate.update(sql, paramsMap);
    } catch (Exception ex) {
      if (ex instanceof SQLException) {
        if (ex.getMessage().contains("Incorrect string value")) {
          item.setContactName(StringUtils.stripAccents(item.getContactName()));
          item.setTitle(StringUtils.stripAccents(item.getTitle()));
          item.setAddress(StringUtils.stripAccents(item.getAddress()));
          item.setDescription(StringUtils.stripAccents(item.getDescription()));
          namedTemplate.update(sql, paramsMap);
        }
      }
    }
  }

  @Override
  public void connectItemToCategory(Long categoryId, Long itemId) {
    final String sql =
        "INSERT INTO crwlr_categories_items_details (category_id, item_id)"
      + " VALUE (:category_id, :item_id)                                  ";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("category_id", categoryId);
    paramsMap.addValue("item_id", itemId);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }
}