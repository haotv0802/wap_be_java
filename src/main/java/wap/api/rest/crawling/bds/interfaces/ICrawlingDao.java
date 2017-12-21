package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.Category;
import wap.api.rest.crawling.bds.beans.Item;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
public interface ICrawlingDao {
  boolean isCategoryExisting(String url);

  void addCategory(Category category);

  void updateCategory(Category category);

  boolean isItemExisting(String url, String categoryUrl);

  void addItem(Item product, String vendorName);

  void updateItem(Item product, String vendorName);
}
