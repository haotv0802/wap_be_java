package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.CrawlingTracking;
import wap.api.rest.crawling.bds.beans.Item;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
public interface ICrawlingDao {
  void addCrawlingTracking(CrawlingTracking crawlingTracking);

  Long isLocationExisting(String district, String city);

  Long addLocation(String district, String city);

  Long isItemExisting(String url);

  Boolean isItemLinkedToCategory(Long id, Long categoryId);

  void addItem(Item product);

  void updateItem(Item product);

  void connectItemToCategory(Long categoryId, Long itemId);
}
