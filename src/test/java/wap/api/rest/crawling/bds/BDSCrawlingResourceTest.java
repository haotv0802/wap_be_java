package wap.api.rest.crawling.bds;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.testng.annotations.Test;
import wap.api.rest.TestBase;
import wap.api.rest.crawling.DocumentationBase;
import wap.api.rest.crawling.bds.beans.Criterion;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by haho on 19/10/2017.
 */
public class BDSCrawlingResourceTest extends DocumentationBase {

  @Test
  public void testCrawlingData() throws Exception {
    mockMvc
        .perform(get("/svc/bds/crawler/crawlingData")
                .header("Accept-Language", "en")
                .header("X-AUTH-TOKEN", authTokenService.getAuthToken())
//    .param("link", "https://batdongsan.com.vn/cho-thue-can-ho-chung-cu/p613")
//                .param("link", "https://batdongsan.com.vn/ban-dat-dat-nen-tp-hcm")

            .param("link", "https://batdongsan.com.vn/ban-nha-rieng-tp-hcm")
//              .param("link", "https://batdongsan.com.vn/ban-can-ho-chung-cu-tp-hcm")
            .param("recrawl", "true")
        )
        .andExpect(status().is(200))
    ;

  }

  @Test
  public void testGetCrawledData() throws Exception {
    Criterion criterion = new Criterion();
    criterion.setPersonName("HaoHo");
    criterion.setCity("Hồ Chí Minh");
//    criterion.setDistrict("Quận Gò Vấp");
//    criterion.setAcreageLessThan("100");
//    criterion.setAcreageLargerThan("80");
    criterion.setPriceLargerThan("15000000");
    criterion.setPriceLessThan("3000000");

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
    criterion.setPersonName("Thanh_BinhThanh");
    criterion.setCity("Hồ Chí Minh");
//    criterion.setDistrict("Quận Gò Vấp");
//    criterion.setDistrict("Quận 12");
    criterion.setDistrict("Quận Bình Thạnh");

//    criterion.setAcreageLessThan("100");
//    criterion.setAcreageLargerThan("80");
//    criterion.setPriceLargerThan("1500000");
//    criterion.setPriceLessThan("3000000");

    mockMvc
        .perform(post("/svc/bds/crawler/exportCrawledData")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsBytes(criterion))
        )
        .andExpect(status().is(200))
    ;
  }
}