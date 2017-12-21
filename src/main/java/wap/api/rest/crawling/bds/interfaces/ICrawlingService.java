package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.Vendor;

import java.util.List;
import java.util.Map;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
public interface ICrawlingService {
  Map<String, Vendor> saveCrawledData(List<String> pages);
}
