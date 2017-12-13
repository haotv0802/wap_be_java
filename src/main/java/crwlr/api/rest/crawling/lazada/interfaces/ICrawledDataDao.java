package crwlr.api.rest.crawling.lazada.interfaces;

import crwlr.api.rest.crawling.lazada.beans.VendorPresenter;
import crwlr.api.rest.crawling.lazada.beans.VendorProductPresenter;

import java.util.List;

/**
 * Date: 10/20/2017 Time: 9:44 AM
 *
 * @author haho
 */
public interface ICrawledDataDao {
  List<VendorProductPresenter> getAllVendorProducts();

  List<VendorPresenter> getAllVendors();
}
