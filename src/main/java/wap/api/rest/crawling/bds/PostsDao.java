package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.interfaces.IPostsDao;
import wap.common.dao.DaoUtils;

/**
 * Created by haoho on 3/27/18 17:18.
 */
@Repository("bdsPostsDao")
public class PostsDao implements IPostsDao {

  private static final Logger LOGGER = LogManager.getLogger(CrawledDataDao.class);

  private final NamedParameterJdbcTemplate namedTemplate;

  @Autowired
  public PostsDao(NamedParameterJdbcTemplate namedTemplate) {
    Assert.notNull(namedTemplate);

    this.namedTemplate = namedTemplate;
  }

  @Override
  public Integer getPostsCount() {
    final String sql =
        "SELECT COUNT(*) FROM crwlr_posts";
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());
    return namedTemplate.queryForObject(sql, paramsMap, Integer.class);
  }

}