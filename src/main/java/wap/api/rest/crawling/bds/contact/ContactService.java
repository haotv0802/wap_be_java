package wap.api.rest.crawling.bds.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;

import java.util.List;

/**
 * Created by haoho on 5/8/18 09:53.
 */
@Service("bdsContactService")
public class ContactService implements IContactService {

  private final IContactDao contactDao;

  @Autowired
  public ContactService(@Qualifier("bdsContactDao") IContactDao contactDao) {
    Assert.notNull(contactDao);

    this.contactDao = contactDao;
  }

  @Override
  public List<ContactPresenter> getContacts() {
    return this.contactDao.getContacts();
  }
}
