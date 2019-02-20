package wap.api.rest.crawling.bds.post;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wap.api.rest.auth.ISlice;
import wap.api.rest.auth.Slice;
import wap.api.rest.crawling.bds.post.beans.PostPresenter;
import wap.common.WapStringUtils;
import wap.common.dao.DaoUtils;

import java.util.List;

/**
 * Created by haoho on 11/9/18 11:42.
 */
@Repository("bdsPostDao")
public class PostDao implements IPostDao {

  private static final Logger LOGGER = LogManager.getLogger(PostDao.class);

  private final NamedParameterJdbcTemplate namedTemplate;

  public PostDao(NamedParameterJdbcTemplate namedTemplate) {
    this.namedTemplate = namedTemplate;
  }

  @Override
  public Integer getPostsCountByContactId(Long contactId) {
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    String whereClause = "";

    if (null != contactId) {
      whereClause += "AND contact_id = :contactId ";
      paramsMap.addValue("contactId", contactId);
    }

    final String sql = String.format("SELECT COUNT(*) FROM crwlr_posts WHERE 1 = 1  %s", whereClause);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class);
  }

  @Override
  public Integer getNumberOfPosts(String date) {
    final String sql = "SELECT COUNT(*) FROM crwlr_posts WHERE publish_date = :date";

    final MapSqlParameterSource map = new MapSqlParameterSource();
    map.addValue("date", date);

    DaoUtils.debugQuery(LOGGER, sql, map.getValues());

    return namedTemplate.queryForObject(sql, map, Integer.class);
  }

  @Override
  public Integer getNumberOfPostsByMonth(Integer month, Integer year) {
    final String sql = "SELECT COUNT(*) FROM crwlr_posts WHERE MONTH(publish_date) = :month AND YEAR(publish_date) = :year";

    final MapSqlParameterSource map = new MapSqlParameterSource();
    map.addValue("month", month);
    map.addValue("year", year);

    DaoUtils.debugQuery(LOGGER, sql, map.getValues());

    return namedTemplate.queryForObject(sql, map, Integer.class);
  }

  @Override
  public ISlice<PostPresenter> getPostsByContactId(Pageable pageable, Long contactId) {
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    String whereClause = "";

    if (null != contactId) {
      whereClause += "AND contact_id = :contactId ";
      paramsMap.addValue("contactId", contactId);
    }

    String sql = String.format(
              "SELECT                 "
            + "    id,                "
            + "    name,              "
            + "    address,           "
            + "    description,       "
            + "    contact_name,      "
            + "    contact_number,    "
            + "    contact_email,     "
            + "    acreage,           "
            + "    price,             "
            + "    publish_date,      "
            + "    end_date,          "
            + "    url,               "
            + "    location_id,       "
            + "    source,            "
            + "    type,              "
            + "    property_type,     "
            + "    contact_id,        "
            + "    created_at,        "
            + "    updated_at         "
            + "FROM                   "
            + "    crwlr_posts        "
            + " WHERE 1 = 1   %s      ", whereClause)
        ;

    String fooSQL = buildSQLWithPaging(sql, pageable);


    DaoUtils.debugQuery(LOGGER, fooSQL, paramsMap.getValues());
    List<PostPresenter> list = namedTemplate.query(fooSQL, paramsMap, (rs, i) -> {
      PostPresenter postPresenter = new PostPresenter();
      postPresenter.setId(rs.getLong("id"));
      postPresenter.setName(rs.getString("name"));
      postPresenter.setAddress(rs.getString("address"));
      postPresenter.setDescription(rs.getString("description"));
      postPresenter.setContactName(rs.getString("contact_name"));
      postPresenter.setContactNumber(rs.getString("contact_number"));
      postPresenter.setContactEmail(rs.getString("contact_email"));
      postPresenter.setAcreage(rs.getDouble("acreage"));
      postPresenter.setPrice(rs.getDouble("price"));
      postPresenter.setPublishDate(rs.getDate("publish_date"));
      postPresenter.setEndDate(rs.getDate("end_date"));
      postPresenter.setUrl(rs.getString("url"));
      postPresenter.setLocationId(rs.getLong("location_id"));
      postPresenter.setSource(rs.getString("source"));
      postPresenter.setType(rs.getString("type"));
      postPresenter.setPropertyType(rs.getString("property_type"));
      postPresenter.setContactId(rs.getLong("contact_id"));
      postPresenter.setCreatedAt(rs.getDate("created_at"));
      postPresenter.setUpdatedAt(rs.getDate("updated_at"));

      return postPresenter;
    });

    boolean hasNext = list.size() > pageable.getPageSize();

    if (hasNext) {
      list.remove(pageable.getPageSize());
    }

    ISlice<PostPresenter> postPresenters = new Slice(list, pageable, hasNext,
        this.getPostsCountByContactId(contactId));
    return postPresenters;
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
