package wap.api.rest.crawling.bds.contact;

import org.springframework.data.domain.Pageable;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;
import wap.api.rest.crawling.bds.contact.beans.ContactUpdate;

import java.io.InputStream;
import java.util.List;

/**
 * Created by haoho on 5/8/18 09:38.
 */
public interface IContactService {
  ISlice<ContactPresenter> getContacts(Pageable pageable, String name, String phone, String email, String type, String manualCheck, String emailExisting);

  void updateContacts(List<ContactUpdate> contacts);

  void updateEmailStatusWithCSV(InputStream inputStream, String status);
}
