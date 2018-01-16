package wap.api.rest.crawling.bds;

import org.springframework.http.MediaType;
import org.testng.annotations.Test;
import wap.api.rest.TestBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haho on 19/10/2017.
 */
public class BDSExportingResourceTest extends TestBase {

  @Test
  public void testExportOwnerContacts() throws Exception {
    mockMvc
        .perform(get("/svc/bds/crawler/exportOwnerContacts")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is(200))
    ;
  }

  @Test
  public void testEmail() throws Exception {
    mockMvc
        .perform(get("/svc/bds/crawler/testEmail")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().is(200))
    ;
  }
}