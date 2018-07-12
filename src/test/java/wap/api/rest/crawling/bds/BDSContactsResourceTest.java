package wap.api.rest.crawling.bds;

import org.testng.annotations.Test;
import wap.api.rest.TestBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haho on 03/08/2018.
 */
public class BDSContactsResourceTest extends TestBase {

  @Test
  public void testGetContacts() throws Exception {
    mockMvc
        .perform(get("/svc/bds/contact/list")
            .param("page", "1")
            .param("name", "h")
        )
        .andExpect(status().is(200))
    ;
  }
}