package wap.api.rest.crawling.bds.customer;

import org.springframework.data.domain.Pageable;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.customer.beans.CustomerAdd;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;

/**
 * Created by haoho on 10/17/18 16:48.
 */
public interface ICustomerDao {
  ISlice<CustomerPresenter> getCustomers(Pageable pageable, String name, String phone, String email);

  void updateCustomer(CustomerPresenter customer);

  Long addCustomer(CustomerAdd customer);

  void deleteCustomer(int customerId);

  Boolean checkPhoneExisting(String phone);

  Boolean checkEmailExisting(String email);

  Boolean checkPhoneExistingExceptId(String phone, Long id);

  Boolean checkEmailExistingExceptId(String email, Long id);
}
