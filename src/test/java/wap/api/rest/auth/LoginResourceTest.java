package wap.api.rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.testng.annotations.Test;
import wap.api.rest.TestBase;
import wap.api.rest.auth.beans.Credentials;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haho on 19/10/2017.
 */
public class LoginResourceTest extends TestBase {

  @Test
  public void testLogin() throws Exception {
    Credentials c = new Credentials();
    c.setUsername("haoho");
    c.setPassword("admin");

    mockMvc
        .perform(post("/svc/login")
            .header("Accept-Language", "en")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(c))
        )
        .andExpect(status().is(200))
    ;
  }
}