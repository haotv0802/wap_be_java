package wap.api.rest.crawling.bds;

import org.springframework.http.MediaType;
import org.testng.annotations.Test;
import wap.api.rest.TestBase;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

  @Test
  public void testAddCustomer() throws Exception {
    CustomerPresenter customer = new CustomerPresenter();
    customer.setName("new name");
    customer.setEmail("new email");
    customer.setPhone("new phone");

    mockMvc
        .perform(put("/svc/bds/customer/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customer))
        )
        .andExpect(status().is(201))
    ;
  }
}
