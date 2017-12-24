package wap.api.rest.crawling.bds.beans;

import java.util.Date;

/**
 * Date: 10/19/2017 Time: 5:00 PM
 * This Category is to be stored in Database (Not for presentation on Front-end).
 *
 * @author haho
 */
public class CrawlingTracking {
  private Long categoryId;
  private Date crawlingStart;
  private Date crawlingEnd;

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public Date getCrawlingStart() {
    return crawlingStart;
  }

  public void setCrawlingStart(Date crawlingStart) {
    this.crawlingStart = crawlingStart;
  }

  public Date getCrawlingEnd() {
    return crawlingEnd;
  }

  public void setCrawlingEnd(Date crawlingEnd) {
    this.crawlingEnd = crawlingEnd;
  }
}