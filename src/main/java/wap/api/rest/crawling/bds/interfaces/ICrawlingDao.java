package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.Category;
import wap.api.rest.crawling.bds.beans.Item;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
public interface ICrawlingDao {
  boolean isVendorExisting(String name);

  void addVendor(Category category);

  void updateVendor(Category category);

  boolean isProductExisting(String name, String vendorName, String link);

  void addVendorProduct(Item product, String vendorName);

  void updateVendorProduct(Item product, String vendorName);
}
