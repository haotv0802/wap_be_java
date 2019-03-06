package wap.api.rest.crawling.bds;

import com.fasterxml.jackson.databind.type.TypeFactory;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.Test;
import wap.api.rest.TestBase;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;
import wap.api.rest.crawling.bds.customer.beans.CustomerUpdate;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    customer.setName("dsdsfdsf");
    customer.setEmail("hao@.com");
    customer.setPhone("123");

    mockMvc
        .perform(post("/svc/bds/customer/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customer))
        )
        .andExpect(status().is(201))
    ;
  }

  @Test
  public void testUpdateCustomer() throws Exception {

    MvcResult result = mockMvc
        .perform(get("/svc/bds/customer/list")
            .param("page", "1")
            .param("name", "h")
        )
        .andExpect(status().is(200)).andReturn()
    ;

    JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());

    String resultAsString = jsonObject.getJSONArray("content").toString();
    List<CustomerUpdate> customerPresenters = this.objectMapper.readValue(
        resultAsString,
        TypeFactory.defaultInstance().constructCollectionType(List.class, CustomerUpdate.class)
    );

    List<CustomerUpdate> customerUpdateList = new ArrayList<>();
    CustomerUpdate customerUpdate = customerPresenters.get(0);
    customerUpdate.setUpdated(true);
//    customerUpdate.setName("HAO");
//    customerUpdate.setEmail("yennhi.gamudaland@gmail.com");

    customerUpdateList.add(customerUpdate);


    mockMvc
        .perform(post("/svc/bds/customer/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customerUpdateList))
        )
        .andExpect(status().is(204))
    ;
  }
}
