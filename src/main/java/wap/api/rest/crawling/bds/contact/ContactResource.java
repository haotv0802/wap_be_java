package wap.api.rest.crawling.bds.contact;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;

/**
 * Created by haoho on 5/8/18 09:04.
 */
@RestController("bdsContactResource")
@RequestMapping(path = "/svc/bds/contact")
public class ContactResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  private final IContactService contactService;

  @Autowired
  public ContactResource(@Qualifier("bdsContactService") IContactService contactService) {
    Assert.notNull(contactService);

    this.contactService = contactService;
  }

  @GetMapping("/list")
  public Slice<ContactPresenter> getContacts(Pageable pageable){
    return this.contactService.getContacts(pageable);
  }
}
