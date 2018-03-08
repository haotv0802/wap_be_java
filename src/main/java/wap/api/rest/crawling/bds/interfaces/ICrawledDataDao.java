package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.*;

import java.util.List;

/**
 * Date: 10/20/2017 Time: 9:44 AM
 *
 * @author haho
 */
public interface ICrawledDataDao {
  List<ItemPresenter> getAllItems();

  List<ItemPresenter> getAllItemsByCriterion(Criterion criterion);

  List<ContactPresenter> getPostsManually();

  List<String> getEmails();

  List<Contact> getContacts();

  List<LocationPresenter> getAllLocations();

  List<ContactPresenter> getOwnerContactsByLocation(int locationId);

  List<String> getAllEmailsNotCheckedYet();

  void updateEmailExisting(String email, boolean existing);

  void trackEmailSent(String from, String to, String title, String content);

  boolean checkEmailSentOrNot(String from, String to);

  boolean checkEmailSentOrNotWithTitle(String title, String to);

  List<CityPresenter> getCitiesAndDistricts();
}
