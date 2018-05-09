package wap.api.rest.crawling.bds.contact;

import org.springframework.data.domain.Pageable;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;

import java.util.List;

/**
 * Created by haoho on 5/8/18 09:38.
 */
public interface IContactService {
  List<ContactPresenter> getContacts(Pageable pageable);
}
