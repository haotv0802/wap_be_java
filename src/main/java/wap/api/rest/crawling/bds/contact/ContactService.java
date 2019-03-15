package wap.api.rest.crawling.bds.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Created by haoho on 5/8/18 09:53.
 */
@Service("bdsContactService")
public class ContactService implements IContactService {

  private final IContactDao contactDao;
  private static final char DEFAULT_SEPARATOR = ',';

  @Autowired
  public ContactService(
      @Qualifier("bdsContactDao") IContactDao contactDao
  ) {
    Assert.notNull(contactDao);

    this.contactDao = contactDao;
  }

  @Override
  public ISlice<ContactPresenter> getContacts(Pageable pageable, String name, String phone, String email, String type, String manualCheck, String emailExisting) {
    return this.contactDao.getContacts(pageable, name, phone, email, type, manualCheck, emailExisting);
  }

  @Override
  public void updateContacts(List<ContactPresenter> contacts) {
    for (ContactPresenter contact : contacts) {
      contactDao.updateContact(contact);
    }
  }

  @Override
  public void updateEmailStatusWithCSV(InputStream inputStream, String status) {
    Scanner scanner = new Scanner(inputStream);
    while (scanner.hasNext()) {
      String[] line = scanner.nextLine().split(";");
      if (line[0].equals("subscriber_id")) {
        continue;
      }
      System.out.println("subid: " + line[0] + ", email: " + line[1] + " , phone:" + line[2]);
      this.contactDao.updateEmailStatus(line[1].trim(), status);
    }
    scanner.close();
  }

}
