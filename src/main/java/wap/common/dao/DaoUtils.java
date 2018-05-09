package wap.common.dao;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import java.util.Map;

public final class DaoUtils {

  private DaoUtils() {
  }

  /**
   * Helper class for holding the calculated paging arguments
   */
  public static class PagingIndex {
    int startIdx;
    int endIdx;
    int pageSize;


    public int getStartIdx() {
      return startIdx;
    }

    public void setStartIdx(int startIdx) {
      this.startIdx = startIdx;
    }

    public int getEndIdx() {
      return endIdx;
    }

    public void setEndIdx(int endIdx) {
      this.endIdx = endIdx;
    }

    public int getPageSize() {
      return pageSize;
    }

    public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
    }
  }

  /**
   * Calculating indexes so the fetched result might be with one more then the the size of the page
   *
   * @param pageRequest
   * @return a object holding the calculated indexes
   */
  public static PagingIndex pagingIdxForSlice(Pageable pageRequest) {
    Assert.notNull(pageRequest, "pageRequest must not be null!");

    final int pageSize = pageRequest.getPageSize();
    final int base = pageSize * pageRequest.getPageNumber();

    PagingIndex pi = new PagingIndex();
//    pi.setStartIdx(base + 1);
//    pi.setEndIdx(base + pageSize + 1);
    pi.setPageSize(pageSize + 1);
    pi.setStartIdx(base);
    pi.setEndIdx(base + pageSize);

    return pi;
  }

  public static void debugQuery(Logger logger, String sql, Map<String, Object> bindVars) {
    if (null != logger && logger.isDebugEnabled()) {
      logger.debug(StringUtils.normalizeSpace(sql) + "; bv:" + bindVars);
    }
  }

}
