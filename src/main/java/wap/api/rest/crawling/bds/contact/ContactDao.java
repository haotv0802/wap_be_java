package wap.api.rest.crawling.bds.contact;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wap.api.rest.auth.ISlice;
import wap.api.rest.auth.Slice;
import wap.api.rest.crawling.bds.CrawledDataDao;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;
import wap.common.WapStringUtils;
import wap.common.dao.DaoUtils;

import java.util.List;

/**
 * Created by haoho on 5/8/18 09:56.
 */
@Repository("bdsContactDao")
public class ContactDao implements IContactDao {

  private static final Logger LOGGER = LogManager.getLogger(CrawledDataDao.class);

  private final NamedParameterJdbcTemplate namedTemplate;

  @Autowired
  public ContactDao(NamedParameterJdbcTemplate namedTemplate) {
    this.namedTemplate = namedTemplate;
  }

  private int getContactsCount(String name, String phone, String email, String type, String manualCheck, String emailExisting) {
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    String whereClause = "";

    if (!StringUtils.isEmpty(name)) {
      whereClause += "AND name like :name ";
      paramsMap.addValue("name", "%" + name + "%");
    }
    if (!StringUtils.isEmpty(phone)) {
      whereClause += "AND phone like :phone ";
      paramsMap.addValue("phone", "%" + phone + "%");
    }
    if (!StringUtils.isEmpty(email)) {
      whereClause += "AND email like :email ";
      paramsMap.addValue("email", "%" + email + "%");
    }
    if (!StringUtils.isEmpty(type)) {
      whereClause += "AND type = :type ";
      paramsMap.addValue("type", type);
    }
    if (!StringUtils.isEmpty(manualCheck)) {
      if (manualCheck.equals("NA")) {
        whereClause += "AND manual_check IS NULL ";
      } else {
        whereClause += "AND manual_check = :manualCheck ";
        paramsMap.addValue("manualCheck", manualCheck);
      }
    }
    if (null != emailExisting && !StringUtils.isEmpty(emailExisting)) {
      if (emailExisting.equals("NA")) {
        whereClause += "AND email_existing IS NULL";
      } else {
        whereClause += "AND email_existing = :emailExisting ";
        paramsMap.addValue("emailExisting", emailExisting.equals("YES"));
      }

    }

    final String sql = String.format("SELECT COUNT(*) FROM crwlr_contacts WHERE 1 = 1  %s", whereClause);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class);
  }

  @Override
  public ISlice<ContactPresenter> getContacts(Pageable pageable, String name, String phone, String email, String type, String manualCheck, String emailExisting) {
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    String whereClause = "";

    if (!StringUtils.isEmpty(name)) {
      whereClause += "AND name like :name ";
      paramsMap.addValue("name", "%" + name + "%");
    }
    if (!StringUtils.isEmpty(phone)) {
      whereClause += "AND phone like :phone ";
      paramsMap.addValue("phone", "%" + phone + "%");
    }
    if (!StringUtils.isEmpty(email)) {
      whereClause += "AND email like :email ";
      paramsMap.addValue("email", "%" + email + "%");
    }
    if (!StringUtils.isEmpty(type)) {
      whereClause += "AND type = :type ";
      paramsMap.addValue("type", type);
    }
    if (!StringUtils.isEmpty(manualCheck)) {
      if (manualCheck.equals("NA")) {
        whereClause += "AND manual_check IS NULL ";
      } else {
        whereClause += "AND manual_check = :manualCheck ";
        paramsMap.addValue("manualCheck", manualCheck);
      }
    }
    if (null != emailExisting && !StringUtils.isEmpty(emailExisting)) {
      if (emailExisting.equals("NA")) {
        whereClause += "AND email_existing IS NULL";
      } else {
        whereClause += "AND email_existing = :emailExisting ";
        paramsMap.addValue("emailExisting", emailExisting.equals("YES"));
      }
    }


    String sql = String.format(
        "SELECT                                                                     "
      + "    id,                                                                    "
      + " (SELECT COUNT(*) FROM crwlr_posts p WHERE contact_id = c.id) posts_count, "
      + "    name,                                                                  "
      + "    phone,                                                                 "
      + "    email,                                                                 "
      + "    type,                                                                  "
      + "    manual_check,                                                          "
      + "    email_existing,                                                        "
      + "    latest_item_posted_on,                                                 "
      + "    created_at,                                                            "
      + "    updated_at,                                                            "
      + "    description                                                            "
      + " FROM                                                                      "
      + "    crwlr_contacts c                                                       "
      + " WHERE 1 = 1   %s         ", whereClause)
//            + "WHERE email <> '' and manual_check is not null"
        ;

    String fooSQL = buildSQLWithPaging(sql, pageable);


    DaoUtils.debugQuery(LOGGER, fooSQL, paramsMap.getValues());
    List<ContactPresenter> list = namedTemplate.query(fooSQL, paramsMap, (rs, i) -> {
      ContactPresenter contactPresenter = new ContactPresenter();
      contactPresenter.setId(rs.getLong("id"));
      contactPresenter.setName(rs.getString("name"));
      contactPresenter.setPhone(rs.getString("phone"));
      contactPresenter.setEmail(rs.getString("email"));
      contactPresenter.setType(rs.getString("type"));
      contactPresenter.setPostsCount(rs.getInt("posts_count"));

      if (rs.getString("manual_check") == null) {
        contactPresenter.setManualCheck("NA");
      } else {
        contactPresenter.setManualCheck(rs.getString("manual_check"));
      }


      String emailExistingAsString;
      if (rs.getString("email_existing") == null) {
        emailExistingAsString = "NA";
      } else if (rs.getString("email_existing").equals("0")) {
        emailExistingAsString = "NO";
      } else {
        emailExistingAsString = "YES";
      }
      contactPresenter.setEmailExisting(emailExistingAsString);
      contactPresenter.setLatestItemPostedAt(rs.getDate("latest_item_posted_on"));
      contactPresenter.setCreatedAt(rs.getDate("created_at"));
      contactPresenter.setUpdatedAt(rs.getDate("updated_at"));
      contactPresenter.setDescription(rs.getString("description"));

      return contactPresenter;
    });

    boolean hasNext = list.size() > pageable.getPageSize();

    if (hasNext) {
      list.remove(pageable.getPageSize());
    }

    ISlice<ContactPresenter> contactPresenters = new Slice<>(list, pageable, hasNext,
        this.getContactsCount(name, phone, email, type, manualCheck, emailExisting));
    return contactPresenters;
  }

  @Override
  public void updateContact(ContactPresenter contact) {
    final String sql =
              "UPDATE crwlr_contacts                "
            + "SET                                  "
            + "    name = :name,                    "
            + "    phone = :phone,                  "
            + "    email = :email,                  "
            + "    description = :description,      "
            + "    type = :type,                    "
            + "    manual_check = :manual_check,    "
            + "    email_existing = :email_existing "
            + "WHERE                                "
            + "    id = :id                         "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", contact.getName());
    paramsMap.addValue("phone", contact.getPhone());
    paramsMap.addValue("email", contact.getEmail());
    paramsMap.addValue("description", contact.getDescription());
    paramsMap.addValue("id", contact.getId());

    if (null != contact.getType() && !StringUtils.isEmpty(contact.getType())) {
      paramsMap.addValue("type", contact.getType());
    } else {
      paramsMap.addValue("type", null);
    }

    if (null != contact.getManualCheck() && !StringUtils.isEmpty(contact.getManualCheck())) {
      paramsMap.addValue("manual_check", contact.getManualCheck());
    } else {
      paramsMap.addValue("manual_check", null);
    }

    if (null != contact.getEmailExisting() && !StringUtils.isEmpty(contact.getEmailExisting()) && !contact.getEmailExisting().equals("NA")) {
      if (contact.getEmailExisting().equals("YES"))
        paramsMap.addValue("email_existing", true);
      else if (contact.getEmailExisting().equals("NO")) {
        paramsMap.addValue("email_existing", false);
      }
    } else {
      paramsMap.addValue("email_existing", null);
    }

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public void updateEmailStatus(String email, String status) {
    final String sql = "UPDATE crwlr_contacts SET email_existing = :status, updated_at = now() WHERE email = :email ";

    LOGGER.info("status: " + status.equals("YES"));
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("status", status.equals("YES"));
    paramsMap.addValue("email", email);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public Boolean checkEmailExistingExceptId(String email, Long id) {
    final String sql = "SELECT COUNT(*) FROM crwlr_contacts WHERE email = :email AND id <> :id";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("email", email);
    paramsMap.addValue("id", id);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) > 0;
  }

  private String buildSQLWithPaging(String sql, Pageable pageable) {
    final DaoUtils.PagingIndex pi = DaoUtils.pagingIdxForSlice(pageable);
    LOGGER.info("Sorting: " + getItemsSearchOrder(pageable.getSort()));
    String fooSql = String.format(
        "SELECT foo.* FROM   "
      + "(                   "
      + " %s                 "
      + "ORDER BY %s         "
      + ") foo               "
      + "                    "
      + "LIMIT %d, %d        ",
        sql,
        getItemsSearchOrder(pageable.getSort()),
        pi.getStartIdx(),
        pi.getPageSize()
    );

    return fooSql;
  }

  private String getItemsSearchOrder(Sort sort) {

    String validOrders = "name,phone,posts_count,email";
    String defaultOrderClause = " name ASC";

    return WapStringUtils.populateOrderBy(sort, validOrders, defaultOrderClause);
  }

}