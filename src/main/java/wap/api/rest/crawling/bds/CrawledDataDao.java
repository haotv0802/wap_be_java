package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.beans.ItemPresenter;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataDao;
import wap.common.JdbcUtils;
import wap.common.dao.DaoUtils;

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
  public CrawledDataDao(NamedParameterJdbcTemplate namedTemplate) {
    Assert.notNull(namedTemplate);

    this.namedTemplate = namedTemplate;
  }

  @Override
  public List<ItemPresenter> getAllItems() {
    final String sql =
                    "SELECT                                        "
                  + "    i.name,                                   "
                  + "    i.address,                                "
                  + "    i.contactName,                            "
                  + "    i.contactNumber,                          "
                  + "    i.contactEmail,                           "
                  + "    i.publish_date,                           "
                  + "    i.end_date,                               "
                  + "    i.url                                     "
                  + "FROM                                          "
                  + "    crwlr_items i                             "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.query(sql, paramsMap, (rs, rowNum) -> {
      ItemPresenter presenter = new ItemPresenter();
      presenter.setTitle(rs.getString("name"));
      presenter.setAddress(rs.getString("address"));
      presenter.setContactName(rs.getString("contactName"));
      presenter.setContactEmail(rs.getString("contactEmail"));
      presenter.setContactNumber(rs.getString("contactNumber"));
      presenter.setPublishDate(JdbcUtils.toUtilDate(rs.getDate("publish_date")));
      presenter.setEndDate(JdbcUtils.toUtilDate(rs.getDate("end_date")));
      presenter.setUrl(rs.getString("url"));
      return presenter;
    });
  }

}
