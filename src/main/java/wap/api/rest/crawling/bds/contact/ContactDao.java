package wap.api.rest.crawling.bds.contact;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
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

  @Override
  public Slice<ContactPresenter> getContacts(Pageable pageable) {
    String sql =
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
      + "FROM                      "
      + "    crwlr_contacts        "
//            + "WHERE email <> '' and manual_check is not null"
        ;

    String fooSQL = buildSQLWithPaging(sql, pageable);

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

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

    Slice<ContactPresenter> contactPresenters = new SliceImpl<>(list, pageable, hasNext);
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