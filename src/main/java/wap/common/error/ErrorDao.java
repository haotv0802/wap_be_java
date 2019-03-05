package wap.common.error;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.common.ServiceFault;

/**
 * Created by haho on 10/05/2017.
 * Class for registering errors
 */
@Repository
public class ErrorDao implements IErrorDao {
  private static final Logger logger = LogManager.getLogger(ErrorDao.class);

  private static final String FE_ERROR_PROC = "FE_ERROR";
  private static final String BE_ERROR_PROC = "BE_ERROR";

  private final JdbcTemplate jdbcTemplate;

  private final NamedParameterJdbcTemplate namedTemplate;

  @Autowired
  public ErrorDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedTemplate) {
    Assert.notNull(jdbcTemplate);
    this.jdbcTemplate = jdbcTemplate;

    Assert.notNull(namedTemplate);
    this.namedTemplate = namedTemplate;
  }

  /**
   * limits the lenght of an error message
   *
   * @param orig the original message as string
   * @return the truncated message
   */
  private String prepErrMsg(String orig) {
    if (null == orig) {
      return "";
    }

    return orig.substring(0, orig.length() > 99 ? 99 : orig.length());
  }

  /**
   * Converts a java stack to string
   *
   * @param stack the stack
   * @return a string builder containing the stack
   */
  private StringBuilder prepareStack(StackTraceElement[] stack) {
    StringBuilder stackStr = new StringBuilder();

    if (stack != null) {
      for (StackTraceElement el : stack) {
        stackStr.append(el.toString()).append("\n");
      }
    }
    return stackStr;
  }

  @Override
  public String registerBackEndFault(ServiceFault fault) {
    return registerBackEndFault(fault, null, null);
  }

  @Override
  public String registerBackEndFault(ServiceFault fault, StackTraceElement[] stack) {
    return registerBackEndFault(fault, stack, null);
  }

  @Override
  public String registerBackEndFault(ServiceFault fault, StackTraceElement[] stack, String dump) {
    StringBuilder stackStr = prepareStack(stack);
    String errorMsg = prepErrMsg(fault.getFaultCode() + " : " + fault.getFaultMessage());

    return insertError(errorMsg, dump, BE_ERROR_PROC, stackStr.toString());
  }

  /**
   * Insert error and return the id
   * @param errMsg
   * @param dump
   * @param process
   * @param stack
   * @return
   */
  private String insertError(String errMsg, String dump, String process, String stack) {
    final String sql = "INSERT INTO error_tracking (error_message, stack_trace, user)"
        + "  VALUES (:errorMessage, :stackTrace, :user)                 ";

    MapSqlParameterSource paramsMap = new MapSqlParameterSource()
        .addValue("errorMessage", errMsg)
        .addValue("stackTrace", stack)
        .addValue("user", "haho");

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedTemplate.update(sql, paramsMap, keyHolder, new String[]{"user"});
    final String errorId = String.valueOf(keyHolder.getKey().longValue());
    return errorId;
  }

}