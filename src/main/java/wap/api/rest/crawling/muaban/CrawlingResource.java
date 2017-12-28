package wap.api.rest.crawling.muaban;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
 * Date: 12/28/2017
 *
 * @author haho
 */
@RestController("muabanCrawlingResource")
@RequestMapping(path = "/svc/muaban")
public class CrawlingResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  private final ICrawlingService crawlingService;


  @Autowired
  public CrawlingResource(
      @Qualifier("muabanCrawlingService") ICrawlingService crawlingService
  ) {
    Assert.notNull(crawlingService);

    this.crawlingService = crawlingService;
  }

  @GetMapping("/crawler/crawlingData")
  public Map<String, CrawlingTracking> crawlingData(
      @RequestParam(value = "link", required = false) String link
  ) {
    List<String> pages = new ArrayList<>();
    if (StringUtils.isEmpty(link)) {
      pages.add("https://muaban.net/can-mua-nha-dat-ho-chi-minh-l59-c35");
    } else {
      pages.add(link);
    }
    return this.crawlingService.saveCrawledData(pages);
  }

}