package wap.api.rest.crawling.bds;

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
import wap.api.rest.crawling.bds.beans.CrawlingTracking;
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
  public void addCrawlingTracking(CrawlingTracking crawlingTracking) {
    final String sql =
             "INSERT INTO crwlr_crawling_tracking (name, url, itemsCount, itemsCrawled)"
           + " VALUE (:name, :url, :itemsCount, :itemsCrawled)                         "
        ;

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", crawlingTracking.getName());
    paramsMap.addValue("url", crawlingTracking.getUrl());
    paramsMap.addValue("itemsCount", crawlingTracking.getItemsCount());
    paramsMap.addValue("itemsCrawled", crawlingTracking.getItemsCrawled());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedTemplate.update(sql, paramsMap, keyHolder);
    final Long id = keyHolder.getKey().longValue();
    crawlingTracking.setId(id);
  }

  @Override
  public Long isItemExisting(String url) {
    final String sql =
    "SELECT i.id FROM crwlr_items i where i.url = :url";

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
  public void addItem(Item item) {
    final String sql =
    "INSERT INTO crwlr_items (name, address, contactName, contactNumber, contactEmail, publish_date, end_date, url, acreage, price, district, city)    "
  + " VALUE (:name, :address, :contactName, :contactNumber, :contactEmail, :publishDate, :endDate, :url, :acreage, :price, :district, :city)           ";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", item.getTitle());
    paramsMap.addValue("address", item.getAddress());
//    paramsMap.addValue("description", item.getDescription());
    paramsMap.addValue("contactName", item.getContactName());
    paramsMap.addValue("contactNumber", item.getContactNumber());
    paramsMap.addValue("contactEmail", item.getContactEmail());
    paramsMap.addValue("publishDate", JdbcUtils.toSQLDate(item.getPublishDate()));
    paramsMap.addValue("endDate", JdbcUtils.toSQLDate(item.getEndDate()));
    paramsMap.addValue("url", item.getUrl());
    paramsMap.addValue("acreage", item.getAcreage());
    paramsMap.addValue("price", item.getPrice());
    paramsMap.addValue("district", item.getDistrict());
    paramsMap.addValue("city", item.getCity());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedTemplate.update(sql, paramsMap, keyHolder);
    final Long id = keyHolder.getKey().longValue();
    item.setId(id);
  }

  @Override
  public void updateItem(Item item) {
    final String sql =
        "UPDATE crwlr_items SET name = :name, address = :address, contactNumber = :contactNumber, acreage = :acreage,                      "
      + " price = :price, district = :district, city = :city,                                                                              "
      + " contactEmail = :contactEmail, publish_date = :publish_date, end_date = :end_date, updated = :updated "
      + " WHERE url = :url                                                                                                                 "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", item.getTitle());
    paramsMap.addValue("address", item.getAddress());
//    paramsMap.addValue("description", item.getDescription());
    paramsMap.addValue("contactName", item.getContactName());
    paramsMap.addValue("contactNumber", item.getContactNumber());
    paramsMap.addValue("contactEmail", item.getContactEmail());
    paramsMap.addValue("publish_date", JdbcUtils.toSQLDate(item.getPublishDate()));
    paramsMap.addValue("end_date", JdbcUtils.toSQLDate(item.getEndDate()));
    paramsMap.addValue("url", item.getUrl());
    paramsMap.addValue("acreage", item.getAcreage());
    paramsMap.addValue("price", item.getPrice());
    paramsMap.addValue("district", item.getDistrict());
    paramsMap.addValue("city", item.getCity());
    paramsMap.addValue("updated", new Date());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public void trackingItem(Long crawlingTrackingId, Long itemId) {
    final String sql =
        "INSERT INTO crwlr_crawling_tracking_items_details (crawling_tracking_id, item_id)"
      + " VALUE (:crawling_tracking_id, :item_id)                                         ";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("crawling_tracking_id", crawlingTrackingId);
    paramsMap.addValue("item_id", itemId);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }
}