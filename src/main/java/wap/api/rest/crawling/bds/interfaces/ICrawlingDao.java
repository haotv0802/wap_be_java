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

  Long isCategoryExisting(String url);

  Long isLocationExisting(String district, String city);

  Long addLocation(String district, String city);

  void addCategory(Category category);

  Long isItemExisting(String url);

  Boolean isItemLinkedToCategory(Long id, Long categoryId);

  void addItem(Item product, Category category);

  void updateItem(Item product, Category category);

  void connectItemToCategory(Long categoryId, Long itemId);
}
