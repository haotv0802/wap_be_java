package wap.api.rest.crawling.bds;

import org.testng.annotations.Test;
import wap.api.rest.TestBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haho on 03/08/2018.
 */
public class BDSCrawledResourceTest extends TestBase {

  @Test
  public void testGetCitiesAndDistricts() throws Exception {
    mockMvc
        .perform(get("/svc/bds/crawler/citiesAndDistricts")
        )
        .andExpect(status().is(200))
    ;
  }

  @Test
  public void testGetCrawledData() throws Exception {
    mockMvc
        .perform(get("/svc/bds/crawler/crawledData")
            .param("page", "5")
            .param("size", "10")
        )
        .andExpect(status().is(200))
    ;
  }

  @Test
  public void testGetCrawledDataAsSale() throws Exception {
    mockMvc
        .perform(get("/svc/bds/crawler/crawledData/true")
            .param("page", "1")
            .param("size", "1000")
        )
        .andExpect(status().is(200))
    ;
  }
}