package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.VendorPresenter;
import wap.api.rest.crawling.bds.beans.VendorProductPresenter;

import java.util.List;

/**
 * Date: 10/20/2017 Time: 10:18 AM
 * TODO: WRITE THE DESCRIPTION HERE
 *
 * @author haho
 */
public interface ICrawledDataService {
  List<VendorProductPresenter> getAllVendorProducts();

  List<VendorPresenter> getAllVendors();
}
