package wap.api.rest.crawling.bds;

import org.testng.annotations.Test;
import wap.api.rest.TestBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haoho on 10/17/18 16:34.
 */
public class BDSCustomerResourceTest extends TestBase {
  @Test
  public void testGetCustomers() throws Exception {
    mockMvc
        .perform(get("/svc/bds/customer/list")
            .param("page", "1")
            .param("name", "h")
        )
        .andExpect(status().is(200))
    ;
  }
}
