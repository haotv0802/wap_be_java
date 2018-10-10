package wap.api.rest.crawling.bds.contact;

import org.springframework.data.domain.Pageable;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;

/**
 * Created by haoho on 5/8/18 09:55.
 */
public interface IContactDao {
  ISlice<ContactPresenter> getContacts(Pageable pageable, String name, String phone, String email, String type, String manualCheck, Boolean emailExisting);

  void updateContact(ContactPresenter contact);
}
