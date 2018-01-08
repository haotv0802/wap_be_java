package wap.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.util.SerializationUtils;
import wap.api.rest.auth.beans.Credentials;
import wap.common.dao.DaoUtils;

import java.sql.Blob;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public class LoginDao {
  private static final Logger log = LogManager.getLogger(LoginDao.class);

  @Autowired
  NamedParameterJdbcTemplate namedTemplate;

  /**
   * Row jdbc for advanced use
   */
  @Autowired
  JdbcTemplate jdbcTemplate;

  public CredentialsResult checkCredentials(Credentials credentials) throws Exception {
    final String sql =
        "SELECT user_name, password FROM user_table where user_name = :username and password = :password";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource()
        .addValue("username", credentials.getUsername())
        .addValue("password", credentials.getPassword());
    CredentialsResult result = null;
    try {
      result = namedTemplate.queryForObject(sql, paramsMap, (resultSet, i) -> {
        CredentialsResult result1 = new CredentialsResult();
        result1.setUserLang(resultSet.getString("user_name"));
        return result1;
      });
    } catch (EmptyResultDataAccessException ex) {
      throw new Exception("Username or password is incorrect");
    }
    return result;
  }

  public UserDetailsImpl findOneByUsername(String username) {

    final String sql = "SELECT user_name, password FROM user_table where user_name = :username";

    final MapSqlParameterSource paramsMap = new MapSqlParameterSource()
        .addValue("username", username);

    DaoUtils.debugQuery(log, sql, paramsMap.getValues());

    Collection<? extends GrantedAuthority> authorities = getAuthorities(username);

    UserDetailsImpl userDetails = null;
    try {
      userDetails = namedTemplate.queryForObject(sql, paramsMap, (rs, rowNum) -> {

        //TODO geting lang from authentication object is plain stupid. So, AN by default
        UserDetailsImpl iud = new UserDetailsImpl(rs.getString("user_name"), rs.getString("password"), authorities);

        return iud;
      });
    } catch (EmptyResultDataAccessException e) {
      log.warn("Credentials not found: ");
    } catch (IncorrectResultSizeDataAccessException e) {
      log.warn("Too many results");
    }

    return userDetails;
  }

  private Collection<? extends GrantedAuthority> getAuthorities(String username) {
    final String sql = "SELECT                                                     "
                     + "	r.ROLE_NAME                                              "
                     + "FROM                                                       "
                     + "	(user_role r                                             "
                     + "	INNER JOIN user_role_details d ON r.id = d.role_id)      "
                     + "		INNER JOIN                                             "
                     + "	user_table u ON u.id = d.user_id                         "
                     + "WHERE                                                      "
                     + "	u.user_name = :username                                  "
        ;
    final MapSqlParameterSource paramsMap = new MapSqlParameterSource()
        .addValue("username", username);

    DaoUtils.debugQuery(log, sql, paramsMap.getValues());

    List<GrantedAuthority> authorities = namedTemplate.query(sql, paramsMap, (resultSet, i) -> {
      AuthorityImpl authority = new AuthorityImpl(resultSet.getString("ROLE_NAME"));
      return authority;
    });

    return authorities;
  }

  public Integer storeUserDetailsToToken(TokenType tokenType, UserDetails user, Date expDate) {
//    final String getIdSql = "SELECT v9_auth_token_seq.nextval FROM DUAL";

    final String addTokenSql =
              "INSERT INTO auth_token"
            + "  (TOKEN_TYPE         "
            + "  , AUTH_OBJECT       "
            + "  , EXP_DATE)         "
            + "VALUES                "
            + "  ( ?                 "
            + "  , ?                 "
            + "  , ?)                ";

//    final Long id = jdbcTemplate.queryForObject(getIdSql, Long.class);

    final SqlLobValue sqlLobValue = new SqlLobValue(SerializationUtils.serialize(user));

    DaoUtils.debugQuery(log, addTokenSql, new Object[]{tokenType.value(), "SIPPED_BLOB", expDate});
//    jdbcTemplate.update(addTokenSql, paramsMap);
    jdbcTemplate.update(
        addTokenSql
        , new Object[]{tokenType.value(), sqlLobValue, expDate}
        , new int[]{Types.VARCHAR, Types.BLOB, Types.TIMESTAMP}
    );

    final String sql = "SELECT ID FROM AUTH_TOKEN ORDER BY ID DESC LIMIT 1";
    DaoUtils.debugQuery(log, sql);

    int id = namedTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class);

//    final String sql2 = "SELECT ID, TOKEN_TYPE, AUTH_OBJECT, EXP_DATE FROM AUTH_TOKEN ORDER BY ID DESC LIMIT 1";
//    namedTemplate.queryForObject(sql2, new MapSqlParameterSource(), new RowMapper<CredentialsResult>() {
//
//      @Override
//      public CredentialsResult mapRow(ResultSet resultSet, int i) throws SQLException {
//        log.info(resultSet.getString("ID"));
//        log.info(resultSet.getString("TOKEN_TYPE"));
//        log.info(resultSet.getString("EXP_DATE"));
//        log.info(resultSet.getString("AUTH_OBJECT"));
//        return null;
//      }
//    });
    return id;
  }

  public UserDetails readUserDetailsFromToken(Integer id) {
    final String getTokenSql = "SELECT TOKEN_TYPE, AUTH_OBJECT, EXP_DATE FROM AUTH_TOKEN WHERE ID = ?";
    final Object[] args = {id};

    DaoUtils.debugQuery(log, getTokenSql, args);
    return jdbcTemplate.queryForObject(getTokenSql, args, (resultSet, i) -> {
      final Blob blob = resultSet.getBlob(2);
      return (UserDetails) SerializationUtils.deserialize(blob.getBytes(1, (int) blob.length()));
    });

  }

}


