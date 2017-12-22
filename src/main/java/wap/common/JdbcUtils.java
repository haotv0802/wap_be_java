package wap.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Date:  17/06/2016 Time: 7:35 PM
 */
public final class JdbcUtils {
  private static final Logger LOGGER = LogManager.getLogger(JdbcUtils.class);
  private static final boolean DEBUG = LOGGER.isDebugEnabled();

  private JdbcUtils() {
  }

  /**
   * Adapts java.sql.Date to java.util.Date
   * Useful for retrieving dates from the result set
   *
   * @param dt java.sql.date to adapt
   * @return java.util.date or null
   */
  public static java.util.Date toUtilDate(Date dt) {
    return dt != null ? new java.util.Date(dt.getTime()) : null;
  }

  /**
   * Adapts java.sql.Timestamp to java.util.Date
   * Useful for retrieving timestamps from the result set
   *
   * @param ts java.sql.Timestamp to adapt
   * @return java.util.date or null
   */
  public static java.util.Date toUtilDate(Timestamp ts) {
    return ts != null ? new java.util.Date(ts.getTime()) : null;
  }

  public static java.sql.Date toSQLDate(java.util.Date date) {
    return date != null ? new java.sql.Date(date.getTime()) : null;
  }
}