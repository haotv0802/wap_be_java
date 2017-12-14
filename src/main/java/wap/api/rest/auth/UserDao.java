package wap.api.rest.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.api.rest.auth.beans.Credentials;
import wap.api.rest.auth.beans.User;
import wap.api.rest.auth.interfaces.ILoginDao;
import wap.api.rest.auth.interfaces.IUserDao;
import wap.common.dao.DaoUtils;

/**
 * Date: 12/14/2017
 *
 * @author haho
 */
@Repository("userDao")
public class UserDao implements IUserDao {
  private static final Logger LOGGER = LogManager.getLogger(UserDao.class);

  private final NamedParameterJdbcTemplate namedTemplate;

  @Autowired
  public UserDao(NamedParameterJdbcTemplate namedTemplate) {
    Assert.notNull(namedTemplate);

    this.namedTemplate = namedTemplate;
  }

  @Override
  public void signup(User user) {
    final String sql =
              "INSERT INTO aspire_users (email, password) values (:email, :pass)";
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("email", user.getUsername());
    paramsMap.addValue("pass", user.getPassword());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    namedTemplate.update(sql, paramsMap);
  }

  @Override
  public boolean isUserExisting(String username) {
    final String sql =
              "SELECT                      "
            + "    COUNT(*)                "
            + "FROM                        "
            + "    aspire_users            "
            + "WHERE                       "
            + "    email = :email          "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("email", username);

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) == 1;
  }
}