package wap.api.rest.crawling.muaban;

import org.testng.annotations.Test;
import wap.api.rest.TestBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haho on 12/28/2017.
 */
public class MuaBanCrawlingResourceTest extends TestBase {

  @Test
  public void testCrawlingData() throws Exception {
    mockMvc
        .perform(get("/svc/muaban/crawler/crawlingData")
            .param("link", "https://muaban.net/can-mua-nha-dat-ho-chi-minh-l59-c35?cp=29")
        )
        .andExpect(status().is(200))
    ;
  }

}