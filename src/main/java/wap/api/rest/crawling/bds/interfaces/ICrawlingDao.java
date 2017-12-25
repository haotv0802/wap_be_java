package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.Category;
import wap.api.rest.crawling.bds.beans.CrawlingTracking;
import wap.api.rest.crawling.bds.beans.Item;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
public interface ICrawlingDao {
  void addCrawlingTracking(CrawlingTracking crawlingTracking);

  void addCategory(Category category);

  Long isItemExisting(String url);

  void addItem(Item product);

  void updateItem(Item product);

  void trackingItem(Long crawlingTrackingId, Long itemId);

  void connectItemToCategory(Long categoryId, Long itemId);
}
