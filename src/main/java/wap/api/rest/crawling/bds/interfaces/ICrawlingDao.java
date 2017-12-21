package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.Vendor;
import wap.api.rest.crawling.bds.beans.VendorProduct;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
public interface ICrawlingDao {
  boolean isVendorExisting(String name);

  void addVendor(Vendor vendor);

  void updateVendor(Vendor vendor);

  boolean isProductExisting(String name, String vendorName, String link);

  void addVendorProduct(VendorProduct product, String vendorName);

  void updateVendorProduct(VendorProduct product, String vendorName);
}
