package wap.api.rest.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.api.rest.auth.beans.Credentials;
import wap.api.rest.auth.interfaces.ILoginDao;
import wap.common.dao.DaoUtils;

/**
 * Date: 12/14/2017
 *
 * @author haho
 */
@Repository("loginDao")
public class LoginDao implements ILoginDao {
  private static final Logger LOGGER = LogManager.getLogger(LoginDao.class);

  private final NamedParameterJdbcTemplate namedTemplate;

  @Autowired
  public LoginDao(NamedParameterJdbcTemplate namedTemplate) {
    Assert.notNull(namedTemplate);

    this.namedTemplate = namedTemplate;
  }

  @Override
  public boolean login(Credentials credentials) {
    final String sql =
          "SELECT                      "
        + "    COUNT(*)                "
        + "FROM                        "
        + "    aspire_users            "
        + "WHERE                       "
        + "    email = :email          "
        + "        AND password = :pass"
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource();
    paramsMap.addValue("email", credentials.getUsername());
    paramsMap.addValue("pass", credentials.getPassword());

    DaoUtils.debugQuery(LOGGER, sql, paramsMap.getValues());

    return namedTemplate.queryForObject(sql, paramsMap, Integer.class) == 1;
  }
}