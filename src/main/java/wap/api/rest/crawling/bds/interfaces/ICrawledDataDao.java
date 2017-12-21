package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.CategoryPresenter;
import wap.api.rest.crawling.bds.beans.ItemPresenter;

import java.util.List;

/**
 * Date: 10/20/2017 Time: 9:44 AM
 *
 * @author haho
 */
public interface ICrawledDataDao {
  List<ItemPresenter> getAllVendorProducts();

  List<CategoryPresenter> getAllVendors();
}
