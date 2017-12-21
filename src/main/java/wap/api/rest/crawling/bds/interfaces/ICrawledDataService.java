package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.CategoryPresenter;
import wap.api.rest.crawling.bds.beans.ItemPresenter;

import java.util.List;

/**
 * Date: 10/20/2017 Time: 10:18 AM
 * TODO: WRITE THE DESCRIPTION HERE
 *
 * @author haho
 */
public interface ICrawledDataService {
  List<ItemPresenter> getAllVendorProducts();

  List<CategoryPresenter> getAllVendors();
}
