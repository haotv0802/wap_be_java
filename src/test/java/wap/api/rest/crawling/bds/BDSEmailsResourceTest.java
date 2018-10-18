package wap.api.rest.crawling.bds;

import org.springframework.http.MediaType;
import org.testng.annotations.Test;
import wap.api.rest.TestBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haho on 19/10/2017.
 */
public class BDSEmailsResourceTest extends TestBase {

  @Test
  public void testSendAds() throws Exception {
    mockMvc
        .perform(get("/svc/bds/crawler/sendAds")
        )
        .andExpect(status().is(200))
    ;
  }

  @Test
  public void testSendAdsToCustomers() throws Exception {
    mockMvc
        .perform(get("/svc/bds/crawler/sendAdsToCustomers")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is(200))
    ;
  }

  @Test
  public void testSendAdsBlockEToCustomers() throws Exception {
    mockMvc
        .perform(get("/svc/bds/crawler/sendAdsBlockEToCustomers")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is(200))
    ;
  }


  @Test
  public void testSendAdsEmeraldToCustomers() throws Exception {
    mockMvc
        .perform(get("/svc/bds/emails/sendAdsEmeraldToCustomers")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is(200))
    ;
  }
}