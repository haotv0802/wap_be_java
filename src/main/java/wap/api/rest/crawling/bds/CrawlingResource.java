package wap.api.rest.crawling.bds;

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
import wap.api.rest.crawling.bds.beans.Category;
import wap.api.rest.crawling.bds.beans.ItemPresenter;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataService;
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
  public Map<String, Category> crawlingData(
      @RequestParam(value = "link", required = false) String link
  ) {
    List<String> pages = new ArrayList<>();
    if (StringUtils.isEmpty(link)) {
      pages.add("https://batdongsan.com.vn/ban-nha-rieng-tp-hcm/p860");
//      pages.add("https://www.lazada.sg/empire-13");
//      pages.add("https://www.lazada.sg/boom_");
//      pages.add("https://www.lazada.sg/the-bro-store");
//      pages.add("https://www.lazada.sg/taka-jewellery1");
//      pages.add("https://www.lazada.sg/crystalawaking");
//      pages.add("https://www.lazada.sg/nicee-shop");
//      pages.add("https://www.lazada.sg/itechcool");
//      pages.add("https://www.lazada.sg/selffix-pte-ltd");
//      pages.add("https://www.lazada.sg/originalfook");
    } else {
      pages.add(link);
    }
    return this.crawlingService.saveCrawledData(pages);
  }

}