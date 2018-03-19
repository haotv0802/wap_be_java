package wap.api.rest.crawling.bds.interfaces;

import com.sun.org.apache.xpath.internal.operations.Bool;
import wap.api.rest.crawling.bds.beans.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * Date: 10/20/2017 Time: 10:18 AM
 *
 * @author haho
 */
public interface ICrawledDataService {
  List<ItemPresenter> getAllItems();

  List<ItemPresenter> getAllItemsByCriterion(Criterion criterion);

  List<ItemPresenter> exportCrawledData(Criterion criterion) throws IOException;

  List<String> getEmails();

  void sendAdsToCustomers() throws MessagingException;

  void sendAdsBlockEToCustomers() throws MessagingException;

  List<ContactPresenter> getPostsManually(Criterion criterion) throws IOException ;

  List<Contact> getContacts();

  void exportOwnerContacts() throws IOException;

  void exportContacts(String email, String city, Boolean onlyNewData) throws IOException;

  void testEmail() throws MessagingException, InterruptedException;

  List<CityPresenter> getCitiesAndDistricts();
}
