package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.beans.*;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataDao;
import wap.common.WapStringUtils;
import wap.common.JdbcUtils;
import wap.common.dao.DaoUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: 10/20/2017 Time: 9:56 AM
 * The DAO is responsible for querying data including vendors and products crawled from given pages.
 *
 * @author haho
 */
@Repository("bdsCrawledDataDao")
public class CrawledDataDao implements ICrawledDataDao {
  private static final Logger LOGGER = LogManager.getLogger(CrawledDataDao.class);

  private final NamedParameterJdbcTemplate namedTemplate;
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  public CrawledDataDao(NamedParameterJdbcTemplate namedTemplate) {
    Assert.notNull(namedTemplate);

    this.namedTemplate = namedTemplate;
  }

  private String buildSQLWithPaging(String sql, Pageable pageable) {
    final DaoUtils.PagingIndex pi = DaoUtils.pagingIdxForSlice(pageable);
    String fooSql = String.format(
        "SELECT foo.* FROM   "
            + "(                   "
            + " %s                 "
            + "ORDER BY %s         "
            + ") foo               "
            + "                    "
            + "LIMIT %d, %d        ",
        sql,
        getPostsSearchOrder(pageable.getSort()),
        pi.getStartIdx(),
        pi.getPageSize()
    );

    return fooSql;
  }

  private String getPostsSearchOrder(Sort sort) {

    String validOrders = "name";
    String defaultOrderClause = " name ASC";

    return WapStringUtils.populateOrderBy(sort, validOrders, defaultOrderClause);
  }

  @Override
  public List<ItemPresenter> getAllPosts(Pageable pageable) {
    final String sql =
              "SELECT                                                    "
            + "    p.name,                                               "
            + "    p.address,                                            "
            + "    p.description,                                        "
            + "    l.city,                                               "
            + "    l.district,                                           "
            + "    c.name contact_name,                                  "
            + "    c.phone contact_number,                               "
            + "    c.email contact_email,                                "
            + "    p.acreage,                                            "
            + "    p.price,                                              "
            + "    p.publish_date,                                       "
            + "    p.end_date,                                           "
            + "    p.url,                                                "
            + "    p.type,                                               "
            + "    p.property_type                                       "
            + "FROM                                                      "
            + "    (crwlr_posts p                                        "
            + "    INNER JOIN crwlr_locations l ON p.location_id = l.id) "
            + "        INNER JOIN                                        "
            + "    crwlr_contacts c ON c.id = p.contact_id               "
            + "  WHERE c.email <> '' AND c.name <> ''                    "
                      ;
    String fooSQL = buildSQLWithPaging(sql, pageable);

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, fooSQL, paramsMap.getValues());

    return namedTemplate.query(fooSQL, paramsMap, (rs, rowNum) -> {
      ItemPresenter presenter = new ItemPresenter();
      presenter.setTitle(rs.getString("name"));
      presenter.setAddress(rs.getString("address"));
      presenter.setDescription(rs.getString("description"));
      presenter.setCity(rs.getString("city"));
      presenter.setDistrict(rs.getString("district"));
      presenter.setPrice(rs.getBigDecimal("price"));
      presenter.setAcreage(rs.getBigDecimal("acreage"));
      presenter.setContactName(rs.getString("contact_name"));
      presenter.setContactEmail(rs.getString("contact_email"));
      presenter.setContactNumber(rs.getString("contact_number"));
      presenter.setPublishDate(JdbcUtils.toUtilDate(rs.getDate("publish_date")));
      presenter.setEndDate(JdbcUtils.toUtilDate(rs.getDate("end_date")));
      presenter.setUrl(rs.getString("url"));
      presenter.setType(rs.getString("type"));
      presenter.setPropertyType(rs.getString("property_type"));

      return presenter;
    });
  }

  @Override
  public List<ItemPresenter> getAllItemsByCriterion(Criterion criterion) {
    String sql =
              "SELECT                                         "
            + "    i.name,                                    "
            + "    i.address,                                 "
            + "    i.contact_name,                            "
            + "    i.contact_number,                          "
            + "    i.contact_email,                           "
            + "    i.publish_date,                            "
            + "    i.acreage,                                 "
            + "    i.city,                                    "
            + "    i.district,                                "
            + "    i.price,                                   "
            + "    i.end_date,                                "
            + "    i.end_date,                                "
            + "    i.url                                      "
            + "FROM                                           "
            + "    crwlr_items i inner join crwlr_categories_items_details ci on i.id = ci.item_id  "
            + "WHERE 1 = 1                                                                          "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    if (null != criterion) {
      if (null != criterion.getCity()) {
        sql += " AND city = :city";
        paramsMap.addValue("city", criterion.getCity());
      }
      if (null != criterion.getDistrict()) {
        sql += " AND district = :district";
        paramsMap.addValue("district", criterion.getDistrict());
      }

      if (null != criterion.getPriceLargerThan()) {
        sql += " AND price >= :priceLargerThan";
        paramsMap.addValue("priceLargerThan", criterion.getPriceLargerThan());
      }

      if (null != criterion.getPriceLessThan()) {
        sql += " AND price <= :priceLessThan";
        paramsMap.addValue("priceLessThan", criterion.getPriceLessThan());
      }

      if (null != criterion.getPriceLargerThan()) {
        sql += " AND acreage >= :acreageLargerThan";
        paramsMap.addValue("acreageLargerThan", criterion.getAcreageLargerThan());
      }

      if (null != criterion.getAcreageLessThan()) {
        sql += " AND acreage <= :acreageLessThan";
        paramsMap.addValue("acreageLessThan", criterion.getAcreageLessThan());
      }

    }
    sql +=    " AND ci.category_id = 1                  "
            + " AND i.id IN (SELECT                     "
            + "    MAX(id) nn                           "
            + "FROM                                     "
            + "    crwlr_items                          "
            + "GROUP BY contact_number , contact_email) "
            + " ORDER BY i.price ASC                    "
    ;

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.query(sql, paramsMap, (rs, rowNum) -> {
      ItemPresenter presenter = new ItemPresenter();
      presenter.setTitle(rs.getString("name"));
      presenter.setAddress(rs.getString("address"));
      presenter.setContactName(rs.getString("contact_name"));
      presenter.setContactEmail(rs.getString("contact_email"));
      presenter.setContactNumber(rs.getString("contact_number"));
      presenter.setPublishDate(JdbcUtils.toUtilDate(rs.getDate("publish_date")));
      presenter.setEndDate(JdbcUtils.toUtilDate(rs.getDate("end_date")));
      presenter.setUrl(rs.getString("url"));
      presenter.setCity(rs.getString("city"));
      presenter.setDistrict(rs.getString("district"));
      presenter.setPrice(rs.getBigDecimal("price"));
      presenter.setAcreage(rs.getBigDecimal("acreage"));
      return presenter;
    });
  }

  @Override
  public List<ContactPresenter> getPostsManually() {
    final String sql =
              "select c.id, c.name, c.phone, c.email, c.type, c.latest_item_posted_on from crwlr_contacts c where c.id in                             "
            + "(                                                                                                                                      "
            + "    select p.contact_id from crwlr_posts p inner join crwlr_locations l on l.id = p.location_id where p.property_type <> 'APARTMENT'   "
            + "    and l.city = 'Ho Chi Minh' and l.district = 'Huyen Hoc Mon'                                                                        "
            + ")                                                                                                                                      "
            + "and c.type = 'OWNER' and email <> ''                                                                                                   "
            + "and c.id = (select max(id) from crwlr_contacts where email = c.email group by email )                                                  "
            + "#and c.email = (select email from crwlr_contacts where email = c.email group by email having count(id) > 1)                            "
            + "order by email asc                                                                                                                     "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.query(sql, paramsMap, new RowMapper<ContactPresenter>() {
      @Override
      public ContactPresenter mapRow(ResultSet resultSet, int i) throws SQLException {
        ContactPresenter contact = new ContactPresenter();
        contact.setId(resultSet.getLong("id"));
        contact.setName(resultSet.getString("name"));
        contact.setPhone(resultSet.getString("phone"));
        contact.setEmail(resultSet.getString("email"));
        contact.setType(resultSet.getString("type"));
        contact.setLatestItemPostedOn(resultSet.getDate("latest_item_posted_on"));

        return contact;
      }
    });
  }

  @Override
  public List<String> getEmails() {
    final String sql =
              "SELECT                                                                               "
            + "    email                                                                            "
            + "FROM                                                                                 "
            + "    crwlr_contacts                                                                   "
            + "WHERE                                                                                "
            + "    id IN ( SELECT DISTINCT (contact_id) FROM crwlr_posts WHERE location_id IN (     "
            + "				SELECT id FROM crawler_db_180104.crwlr_locations WHERE city LIKE 'Đong Nai')  "
            + "				AND property_type = 'APARTMENT')                                              "
            + "    AND email <> ''                                                                  "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForList(sql, paramsMap, String.class);
  }

  @Override
  public List<Contact> getContacts() {
    // without URL
//    final String sql =
//              "SELECT                                                                               "
//            + "    id, name, phone, email, type, latest_item_posted_on                              "
//            + "FROM                                                                                 "
//            + "    crwlr_contacts                                                                   "
//            + "WHERE                                                                                "
//            + "    id IN ( SELECT DISTINCT (contact_id) FROM crwlr_posts WHERE location_id IN (     "
//            + "				SELECT id FROM crwlr_locations WHERE city LIKE 'Đong Nai')                    "
//            + "				AND property_type = 'APARTMENT')                                              "
//            + "    AND email <> ''                                                                  "
        ;

    // with URL

        final String sql =
              "SELECT                                                                               "
            + "    c.id, c.name, c.phone, c.email, c.type, c.latest_item_posted_on, p.url           "
            + "FROM                                                                                 "
            + "    crwlr_contacts c INNER JOIN crwlr_posts p ON c.id = p.contact_id                 "
            + "WHERE                                                                                "
            + "    c.id IN ( SELECT DISTINCT (contact_id) FROM crwlr_posts WHERE location_id IN (   "
            + "				SELECT id FROM crwlr_locations WHERE city = 'Ho Chi Minh' AND district = '12')"
            + "				AND property_type = 'APARTMENT')                                              "
            + " AND p.created_at = (SELECT MAX(created_at) FROM crwlr_posts WHERE contact_id = c.id)"
            + "    AND c.email <> ''                                                                "
            ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.query(sql, paramsMap, new RowMapper<Contact>() {
      @Override
      public Contact mapRow(ResultSet resultSet, int i) throws SQLException {
        Contact contact = new Contact();
        contact.setId(resultSet.getLong("id"));
        contact.setName(resultSet.getString("name"));
        contact.setPhone(resultSet.getString("phone"));
        contact.setEmail(resultSet.getString("email"));
        contact.setType(resultSet.getString("type"));
        contact.setLatestItemPostedOn(resultSet.getDate("latest_item_posted_on"));
        contact.setUrl(resultSet.getString("url"));

        return contact;
      }
    });
  }

  @Override
  public List<LocationPresenter> getAllLocations() {
    final String sql =
        "SELECT id, city, district FROM crwlr_locations ORDER BY city, district"
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.query(sql, paramsMap, new RowMapper<LocationPresenter>() {
      @Override
      public LocationPresenter mapRow(ResultSet resultSet, int i) throws SQLException {
        LocationPresenter location = new LocationPresenter();
        location.setId(resultSet.getInt("id"));
        location.setCity(resultSet.getString("city"));
        location.setDistrict(resultSet.getString("district"));

        return location;
      }
    });
  }

  @Override
  public List<LocationPresenter> getAllLocationsByCity(String city) {
    final String sql =
        "SELECT id, city, district FROM crwlr_locations WHERE city = :city ORDER BY city, district"
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("city", city);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.query(sql, paramsMap, new RowMapper<LocationPresenter>() {
      @Override
      public LocationPresenter mapRow(ResultSet resultSet, int i) throws SQLException {
        LocationPresenter location = new LocationPresenter();
        location.setId(resultSet.getInt("id"));
        location.setCity(resultSet.getString("city"));
        location.setDistrict(resultSet.getString("district"));

        return location;
      }
    });
  }

  @Override
  public List<ContactPresenter> getOwnerContactsByLocationAndNoOfPosts(int locationId, int noOfPosts) {
    final String sql =
        "SELECT c.id, c.name, c.phone, c.email, c.type, c.latest_item_posted_on                                                                          "
            + "    ,(SELECT count(*) FROM crwlr_posts WHERE contact_id = c.id AND property_type = 'HOUSE' AND location_id = :locationId) as posts_count  "
            + "    ,(SELECT URL FROM crwlr_posts WHERE contact_id = c.id  order by id desc  LIMIT 0,1) url                                               "
            + "    ,(SELECT price FROM crwlr_posts WHERE contact_id = c.id  order by id desc  LIMIT 0,1) price                                           "
            + "FROM crwlr_contacts c                                                                                                                     "
            + "WHERE c.email <> '' AND manual_check IS NULL AND TYPE = 'OWNER'                                                                           "
//            + "AND c.email NOT LIKE '%gmail.com'                                                                                                             "
            + "    AND (email_existing IS NULL OR email_existing = TRUE)                                                                                 "
            + "    AND (SELECT count(*) FROM crwlr_posts WHERE contact_id = c.id AND property_type = 'HOUSE' AND location_id = :locationId) = :noOfPosts "
            + "    AND (SELECT price FROM crwlr_posts WHERE contact_id = c.id  order by id desc  LIMIT 0,1)  > 4000                                      "
            + "        ORDER BY price desc                                                                                                               "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("locationId", locationId);
    paramsMap.addValue("noOfPosts", noOfPosts);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.query(sql, paramsMap, (resultSet, i) -> {
      ContactPresenter contact = new ContactPresenter();
      contact.setId(resultSet.getLong("id"));
      contact.setName(resultSet.getString("name"));
      contact.setPhone(resultSet.getString("phone"));
      contact.setEmail(resultSet.getString("email"));
      contact.setType(resultSet.getString("type"));
      contact.setPrice(resultSet.getBigDecimal("price"));
      contact.setCount(resultSet.getInt("posts_count"));
      contact.setLatestItemPostedOn(resultSet.getDate("latest_item_posted_on"));
      contact.setPostUrl(resultSet.getString("url"));
      return contact;
    });
  }

  @Override
  public List<ContactPresenter> getOwnerContactsByLocationAndNoOfPostsAndTime(int locationId, int noOfPosts, int year, int month) {
    final String sql =
        "SELECT c.id, c.name, c.phone, c.email, c.type, c.latest_item_posted_on                                                                    "
      + "    ,(SELECT count(*) FROM crwlr_posts WHERE contact_id = c.id AND property_type = 'HOUSE' AND location_id = :locationId                  "
      + "           AND year(publish_date) = :year AND month(publish_date) = :month) as posts_count                                                "
      + "    ,(SELECT URL FROM crwlr_posts WHERE contact_id = c.id  order by id desc  LIMIT 0,1) url                                               "
      + "    ,(SELECT price FROM crwlr_posts WHERE contact_id = c.id  order by id desc  LIMIT 0,1) price                                           "
      + "FROM crwlr_contacts c                                                                                                                     "
      + "WHERE c.email <> '' AND manual_check IS NULL AND TYPE = 'OWNER'                                                                           "
//      + "AND c.email LIKE '%gmail.com'                                                                                                             "
      + "    AND (email_existing IS NULL OR email_existing = TRUE)                                                                                 "
      + "    AND (SELECT count(*) FROM crwlr_posts WHERE contact_id = c.id AND property_type = 'HOUSE' AND location_id = :locationId               "
      + "           AND year(publish_date) = :year AND month(publish_date) = :month) = :noOfPosts                                                  "
//      + "    AND (SELECT price FROM crwlr_posts WHERE contact_id = c.id  order by id desc  LIMIT 0,1)  > 4000                                      "
      + "        ORDER BY posts_count desc                                                                                                         "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("locationId", locationId);
    paramsMap.addValue("noOfPosts", noOfPosts);
    paramsMap.addValue("noOfPosts", noOfPosts);
    paramsMap.addValue("year", year);
    paramsMap.addValue("month", month);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.query(sql, paramsMap, (resultSet, i) -> {
      ContactPresenter contact = new ContactPresenter();
      contact.setId(resultSet.getLong("id"));
      contact.setName(resultSet.getString("name"));
      contact.setPhone(resultSet.getString("phone"));
      contact.setEmail(resultSet.getString("email"));
      contact.setType(resultSet.getString("type"));
      contact.setPrice(resultSet.getBigDecimal("price"));
      contact.setCount(resultSet.getInt("posts_count"));
      contact.setLatestItemPostedOn(resultSet.getDate("latest_item_posted_on"));
      contact.setPostUrl(resultSet.getString("url"));
      return contact;
    });
  }

  @Override
  public List<ContactPresenter> getOwnerContactsByLocation(int locationId) {
    final String sql =
      "SELECT c.id, c.name, c.phone, c.email, c.type, c.latest_item_posted_on                                                                    "
    + "    ,(SELECT count(*) FROM crwlr_posts WHERE contact_id = c.id AND property_type = 'HOUSE' AND location_id = :locationId) as posts_count  "
    + "    ,(SELECT URL FROM crwlr_posts WHERE contact_id = c.id  order by id desc  LIMIT 0,1) url                                               "
    + "    ,(SELECT price FROM crwlr_posts WHERE contact_id = c.id  order by id desc  LIMIT 0,1) price                                           "
    + "FROM crwlr_contacts c                                                                                                                     "
    + "WHERE c.email <> '' AND manual_check IS NULL AND TYPE = 'OWNER'                                                                           "
    + "AND c.email LIKE '%gmail.com'                                                                                                           "
    + "    AND (email_existing IS NULL OR email_existing = TRUE)                                                                                 "
    + "    AND (SELECT count(*) FROM crwlr_posts WHERE contact_id = c.id AND property_type = 'HOUSE' AND location_id = :locationId) = 1          "
    + "        ORDER BY posts_count desc                                                                                                         "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("locationId", locationId);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.query(sql, paramsMap, (resultSet, i) -> {
      ContactPresenter contact = new ContactPresenter();
      contact.setId(resultSet.getLong("id"));
      contact.setName(resultSet.getString("name"));
      contact.setPhone(resultSet.getString("phone"));
      contact.setEmail(resultSet.getString("email"));
      contact.setType(resultSet.getString("type"));
      contact.setPrice(resultSet.getBigDecimal("price"));
      contact.setCount(resultSet.getInt("posts_count"));
      contact.setLatestItemPostedOn(resultSet.getDate("latest_item_posted_on"));
      contact.setPostUrl(resultSet.getString("url"));
      return contact;
    });
  }

  @Override
  public List<String> getAllEmailsNotCheckedYet() {
    final String sql =
        "SELECT distinct email FROM crwlr_contacts where email <> '' AND email_existing IS NULL";
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForList(sql, paramsMap, String.class);
  }

  @Override
  public void updateEmailExisting(String email, boolean existing) {
    final String sql =
        "UPDATE crwlr_contacts SET email_existing = :existing where email = :email";
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    paramsMap.addValue("existing", existing);
    paramsMap.addValue("email", email);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public void trackEmailSent(String from, String to, String title, String content) {
    final String sql =
        "INSERT INTO crwlr_sent_emails_tracking (from_email, to_email, title, content) VALUES (:from_email, :to_email, :title, :content)";
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    paramsMap.addValue("from_email", from);
    paramsMap.addValue("to_email", to);
    paramsMap.addValue("title", title);
    paramsMap.addValue("content", content);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());
    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public boolean checkEmailSentOrNot(String from, String to) {
    final String sql =
        "SELECT COUNT(*) FROM crwlr_sent_emails_tracking WHERE from_email = :from_email and to_email = :to_email";
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    paramsMap.addValue("from_email", from);
    paramsMap.addValue("to_email", to);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());
    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) > 0;
  }

  @Override
  public boolean checkEmailSentOrNotWithTitle(String title, String to) {
    final String sql =
        "SELECT COUNT(*) FROM crwlr_sent_emails_tracking WHERE title = :title and to_email = :to_email";
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    paramsMap.addValue("title", title);
    paramsMap.addValue("to_email", to);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());
    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) > 0;
  }

  @Override
  public List<CityPresenter> getCitiesAndDistricts() {
    String sql = "SELECT distinct city FROM crwlr_locations";

    MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    List<String> cities = namedTemplate.queryForList(sql, paramsMap, String.class);

    List<CityPresenter> locations = new ArrayList<>();

    for (String city : cities) {
      CityPresenter location = new CityPresenter();
      location.setCity(city);
      sql = "SELECT id, district FROM crwlr_locations WHERE city = :city";

      paramsMap = new MapSqlParameterSource();
      paramsMap.addValue("city", city);

      DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

      List<District> districts = namedTemplate.query(sql, paramsMap, new RowMapper<District>() {
        @Override
        public District mapRow(ResultSet resultSet, int i) throws SQLException {
          String name = resultSet.getString("district");
          Integer id = resultSet.getInt("id");
          District dist = new District(id, name);
          return dist;
        }
      });

      location.setDistricts(districts);
      locations.add(location);
    }

    return locations;
  }

  @Override
  public Customer getCustomerByEmail(String email) {
    final String sql =
        "SELECT id, name, phone, email, latest_export_at, created_at, updated_at FROM crwlr_customers WHERE email = :email";
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    paramsMap.addValue("email", email);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    try {
      return namedTemplate.queryForObject(sql, paramsMap, (rs, i) -> {
        Customer customer = new Customer();
        customer.setEmail(rs.getString("email"));
        customer.setName(rs.getString("name"));
        customer.setId(rs.getLong("id"));
        customer.setLatestExportAt(rs.getDate("latest_export_at"));
        customer.setPhone(rs.getString("phone"));
        customer.setUpdatedAt(rs.getDate("updated_at"));
        return customer;
      });
    } catch (EmptyResultDataAccessException ex) {
      return null;
    }
  }

  @Override
  public void trackExport(Long customerId, Long contactId, String fileName) {
    final String sql =
        "INSERT INTO crwlr_exports_tracking (customer_id, contact_id, file_name) VALUE(:customerId, :contactId, :fileName);";
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    paramsMap.addValue("customerId", customerId);
    paramsMap.addValue("contactId", contactId);
    paramsMap.addValue("fileName", fileName);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public boolean isContactExported(Long customerId, Long contactId) {
    final String sql =
        "SELECT COUNT(*) FROM crwlr_exports_tracking WHERE customer_id = :customerId AND contact_id = :contactId";
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    paramsMap.addValue("customerId", customerId);
    paramsMap.addValue("contactId", contactId);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());
    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) > 0;
  }
}