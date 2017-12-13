package crwlr.api.rest.crawling.lazada;

import crwlr.api.rest.TestBase;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haho on 19/10/2017.
 */
public class CrawlingResourceTest extends TestBase {

  @Test
  public void testCrawlingData() throws Exception {
    mockMvc
        .perform(get("/svc/crawler/crawlingData")
//            .param("link", "https://www.lazada.sg/empire-13")
        )
        .andExpect(status().is(200))
    ;
  }

  @Test
  public void testGetCrawledData() throws Exception {
    mockMvc
        .perform(get("/svc/crawler/crawledData")
        )
        .andExpect(status().is(200))
    ;
  }

  @Test
  public void testGetAllVendors() throws Exception {
    mockMvc
        .perform(get("/svc/crawler/vendors")
        )
        .andExpect(status().is(200))
    ;
  }
}