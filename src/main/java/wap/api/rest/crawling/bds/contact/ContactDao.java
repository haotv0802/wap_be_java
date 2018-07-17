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

  private int getContactsCount() {
    final String sql = "SELECT COUNT(*) FROM crwlr_contacts";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class);
  }

  @Override
  public ISlice<ContactPresenter> getContacts(Pageable pageable, String name, String phone, String email, String type, String manualCheck, Boolean emailExisting) {
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
      whereClause += "AND manual_check = :manualCheck ";
      paramsMap.addValue("manualCheck", manualCheck);
    }
    if (null != emailExisting) {
      whereClause += "AND email_existing = :emailExisting ";
      paramsMap.addValue("emailExisting", emailExisting);
    }


    String sql = String.format(
        "SELECT                    "
      + "    id,                   "
      + "    name,                 "
      + "    phone,                "
      + "    email,                "
      + "    type,                 "
      + "    manual_check,         "
      + "    email_existing,       "
      + "    latest_item_posted_on,"
      + "    created_at,           "
      + "    updated_at            "
      + " FROM                     "
      + "    crwlr_contacts        "
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
      contactPresenter.setManualCheck(rs.getString("manual_check"));
      contactPresenter.setEmailExisting(rs.getBoolean("email_existing"));
      contactPresenter.setLatestItemPostedAt(rs.getDate("latest_item_posted_on"));
      contactPresenter.setCreatedAt(rs.getDate("created_at"));
      contactPresenter.setUpdatedAt(rs.getDate("updated_at"));

      return contactPresenter;
    });

    boolean hasNext = list.size() > pageable.getPageSize();

    if (hasNext) {
      list.remove(pageable.getPageSize());
    }

    ISlice<ContactPresenter> contactPresenters = new Slice<>(list, pageable, hasNext, this.getContactsCount());
    return contactPresenters;
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
        getItemsSearchOrder(pageable.getSort()),
        pi.getStartIdx(),
        pi.getPageSize()
    );

    return fooSql;
  }

  private String getItemsSearchOrder(Sort sort) {

    String validOrders = "name";
    String defaultOrderClause = " name ASC";

    return WapStringUtils.populateOrderBy(sort, validOrders, defaultOrderClause);
  }

}