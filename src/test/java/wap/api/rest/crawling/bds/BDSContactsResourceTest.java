package wap.api.rest.crawling.bds;

import com.fasterxml.jackson.databind.type.TypeFactory;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.Test;
import wap.api.rest.TestBase;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;
import wap.api.rest.crawling.bds.contact.beans.ContactUpdate;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

  @Test
  public void testUpdateContacts() throws Exception {
    MvcResult result = mockMvc
        .perform(get("/svc/bds/contact/list")
            .param("page", "1")
            .param("name", "h")
        )
        .andReturn();

    JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());

    String resultAsString = jsonObject.getJSONArray("content").toString();

    List<ContactPresenter> contacts = this.objectMapper.readValue(resultAsString,
        TypeFactory.defaultInstance().
            constructCollectionType(List.class, ContactPresenter.class));

    List<ContactUpdate> contactUpdateList = new ArrayList<>();
    if (contacts.size() > 0) {
      ContactPresenter contactPresenter = contacts.get(0);
      ContactUpdate contactUpdate = new ContactUpdate();
      contactUpdate.setId(contactPresenter.getId());
      contactUpdate.setEmail(contactPresenter.getEmail());
      contactUpdate.setDescription(contactPresenter.getDescription());
      contactUpdate.setEmailExisting(contactPresenter.getEmailExisting());
      contactUpdate.setName(contactPresenter.getName());
      contactUpdate.setPhone(contactPresenter.getPhone());
      contactUpdate.setType(contactPresenter.getType());
      contactUpdate.setManualCheck(contactPresenter.getManualCheck());
      contactUpdate.setCreatedAt(contactPresenter.getCreatedAt());
      contactUpdate.setLatestItemPostedAt(contactPresenter.getLatestItemPostedAt());
      contactUpdate.setPostsCount(contactPresenter.getPostsCount());
      contactUpdate.setUpdated(contactPresenter.getUpdated());
      contactUpdate.setUpdatedAt(contactPresenter.getUpdatedAt());


      // update information
      contactUpdate.setUpdated(true);
      contactUpdate.setEmail("testing_updated" + contactUpdate.getEmail() );

      contactUpdateList.add(contactUpdate);
    }

    mockMvc
        .perform(post("/svc/bds/contact/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(contactUpdateList))
        )
        .andExpect(status().is(204))
    ;
  }
}