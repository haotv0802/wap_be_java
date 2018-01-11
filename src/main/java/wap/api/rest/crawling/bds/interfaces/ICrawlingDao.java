package wap.api.rest.crawling.bds.interfaces;

import wap.api.rest.crawling.bds.beans.Contact;
import wap.api.rest.crawling.bds.beans.CrawlingTracking;
import wap.api.rest.crawling.bds.beans.Item;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
public interface ICrawlingDao {
  void addCrawlingTracking(CrawlingTracking crawlingTracking);

  Long isLocationExisting(String district, String city);

  Contact getContactById(Long id);

  Long addLocation(String district, String city);

  void addContact(Contact contact);

  Long isContactExisting(String phone, String email);

  void updateContact(Contact contact);

  int contactEmailCount(String email);

  Long isItemExisting(String url);

  Boolean isItemLinkedToCategory(Long id, Long categoryId);

  void addItem(Item product);

  void updateItem(Item product);

  void connectItemToCategory(Long categoryId, Long itemId);
}
