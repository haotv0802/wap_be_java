package wap.api.rest.crawling.bds;

import org.testng.annotations.Test;
import wap.api.rest.TestBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haho on 03/08/2018.
 */
public class BDSPostsResourceTest extends TestBase {

  @Test
  public void testGetPostsCount() throws Exception {
    mockMvc
        .perform(get("/svc/bds/posts/count")
        )
        .andExpect(status().is(200))
    ;
  }

  @Test
  public void testGetReport() throws Exception {
    mockMvc
        .perform(get("/svc/bds/post/report?startDate=01/01/2019&&endDate=25/01/2019")
        )
        .andExpect(status().is(200))
    ;
  }
}