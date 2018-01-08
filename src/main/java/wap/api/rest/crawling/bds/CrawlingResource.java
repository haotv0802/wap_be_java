package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wap.api.rest.crawling.bds.beans.CrawlingTracking;
import wap.api.rest.crawling.bds.interfaces.ICrawlingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Date: 12/21/2017
 *
 * @author haho
 */
@RestController("bdsCrawlingResource")
@RequestMapping(path = "/svc/bds")
public class CrawlingResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  private final ICrawlingService crawlingService;


  @Autowired
  public CrawlingResource(
      @Qualifier("bdsCrawlingService") ICrawlingService crawlingService
  ) {
    Assert.notNull(crawlingService);

    this.crawlingService = crawlingService;
  }

  @GetMapping("/crawler/crawlingData")
  public Map<String, CrawlingTracking> crawlingData(
      @RequestParam(value = "link", required = false) String link,
      @RequestParam(value = "recrawl", required = false) boolean recrawl
  ) {
    List<String> pages = new ArrayList<>();
    if (StringUtils.isEmpty(link)) {
        pages.add("https://batdongsan.com.vn/ban-nha-rieng-tp-hcm");
//      pages.add("https://batdongsan.com.vn/nha-dat-ban/p7243");
//      pages.add("https://batdongsan.com.vn/ban-can-ho-chung-cu/p1907");
//      pages.add("https://batdongsan.com.vn/nha-dat-can-mua/p26");
//      pages.add("https://batdongsan.com.vn/nha-dat-can-thue/p19");
    } else {
      pages.add(link);
    }
    return this.crawlingService.saveCrawledData(pages, recrawl);
  }

}