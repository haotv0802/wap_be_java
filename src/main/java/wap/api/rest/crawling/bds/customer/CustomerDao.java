package wap.api.rest.crawling.bds.customer;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wap.api.rest.auth.ISlice;
import wap.api.rest.auth.Slice;
import wap.api.rest.crawling.bds.customer.beans.CustomerAdd;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;
import wap.common.WapStringUtils;
import wap.common.dao.DaoUtils;

import java.util.List;

/**
 * Created by haoho on 10/17/18 16:48.
 */
@Repository("bdsCustomerDao")
public class CustomerDao implements ICustomerDao {

  private static final Logger LOGGER = LogManager.getLogger(CustomerDao.class);

  private final NamedParameterJdbcTemplate namedTemplate;

  @Autowired
  public CustomerDao(NamedParameterJdbcTemplate namedTemplate) {
    this.namedTemplate = namedTemplate;
  }

  private int getCustomersCount(String name, String phone, String email) {
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

    final String sql = String.format("SELECT COUNT(*) FROM crwlr_customers WHERE 1 = 1  %s", whereClause);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class);
  }


  @Override
  public ISlice<CustomerPresenter> getCusomters(Pageable pageable, String name, String phone, String email) {
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

    String sql = String.format(
              "SELECT                    "
            + "    id,                   "
            + "    name,                 "
            + "    phone,                "
            + "    email,                "
            + "    latest_export_at,     "
            + "    created_at,           "
            + "    updated_at            "
            + " FROM                     "
            + "    crwlr_customers       "
            + " WHERE 1 = 1   %s         ", whereClause)
        ;

    String fooSQL = buildSQLWithPaging(sql, pageable);

    DaoUtils.debugQuery(LOGGER, fooSQL, paramsMap.getValues());
    List<CustomerPresenter> list = namedTemplate.query(fooSQL, paramsMap, (rs, i) -> {
      CustomerPresenter customer = new CustomerPresenter();
      customer.setId(rs.getLong("id"));
      customer.setName(rs.getString("name"));
      customer.setPhone(rs.getString("phone"));
      customer.setEmail(rs.getString("email"));
      customer.setLatestExportAt(rs.getDate("latest_export_at"));
      customer.setCreatedAt(rs.getDate("created_at"));
      customer.setUpdatedAt(rs.getDate("updated_at"));

      return customer;
    });

    boolean hasNext = list.size() > pageable.getPageSize();

    if (hasNext) {
      list.remove(pageable.getPageSize());
    }

    ISlice<CustomerPresenter> customers = new Slice<>(list, pageable, hasNext,
        this.getCustomersCount(name, phone, email));
    return customers;
  }

  @Override
  public void updateCustomer(CustomerPresenter customer) {
    final String sql =
              "UPDATE crwlr_customers           "
            + "SET                              "
            + "    name = :name,                "
            + "    phone = :phone,              "
            + "    email = :email               "
            + "WHERE                            "
            + "    id = :id                     "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", customer.getName());
    paramsMap.addValue("phone", customer.getPhone());
    paramsMap.addValue("email", customer.getEmail());
    paramsMap.addValue("id", customer.getId());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public Long addCustomer(CustomerAdd customer) {
    final String sql = "INSERT INTO crwlr_customers (name, phone, email) values (:name, :phone, :email)";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("name", customer.getName());
    paramsMap.addValue("phone", customer.getPhone());
    paramsMap.addValue("email", customer.getEmail());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedTemplate.update(sql, paramsMap, keyHolder);
    return keyHolder.getKey().longValue();
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
