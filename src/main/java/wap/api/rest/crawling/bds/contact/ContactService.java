package wap.api.rest.crawling.bds.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;

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
  public Slice<ContactPresenter> getContacts(Pageable pageable) {
    return this.contactDao.getContacts(pageable);
  }
}
