package wap.api.rest.crawling.bds.contact;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;

/**
 * Created by haoho on 5/8/18 09:38.
 */
public interface IContactService {
  Slice<ContactPresenter> getContacts(Pageable pageable);
}
