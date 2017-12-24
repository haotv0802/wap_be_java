package wap.api.rest.crawling.bds;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.testng.annotations.Test;
import wap.api.rest.TestBase;
import wap.api.rest.crawling.bds.beans.Criterion;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haho on 19/10/2017.
 */
public class CrawlingResourceTest extends TestBase {

  @Test
  public void testCrawlingData() throws Exception {
    mockMvc
        .perform(get("/svc/bds/crawler/crawlingData")
//            .param("link", "https://www.lazada.sg/empire-13")
        )
        .andExpect(status().is(200))
    ;
  }


  @Test
  public void testGetCrawledData() throws Exception {
    Criterion criterion = new Criterion();
    criterion.setCity("Hồ Chí Minh");
//    criterion.setDistrict("Quận Gò Vấp");
    criterion.setAcreageLessThan("100");
    criterion.setAcreageLargerThan("80");
    criterion.setPriceLargerThan("6000000");
    criterion.setPriceLessThan("9000000");

    mockMvc
        .perform(post("/svc/bds/crawler/crawledData")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsBytes(criterion))
        )
        .andExpect(status().is(200))
    ;
  }

  @Test
  public void testExportCrawledData() throws Exception {
    Criterion criterion = new Criterion();
    criterion.setCity("Hồ Chí Minh");
//    criterion.setDistrict("Quận Gò Vấp");
    criterion.setAcreageLessThan("100");
    criterion.setAcreageLargerThan("80");
    criterion.setPriceLargerThan("6000000");
    criterion.setPriceLessThan("9000000");

    mockMvc
        .perform(post("/svc/bds/crawler/exportCrawledData")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsBytes(criterion))
        )
        .andExpect(status().is(200))
    ;
  }
}