package wap.api.rest.crawling.bds.customer;

import org.springframework.data.domain.Pageable;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.customer.beans.CustomerAdd;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;
import wap.api.rest.crawling.bds.customer.beans.CustomerUpdate;

/**
 * Created by haoho on 10/17/18 16:48.
 */
public interface ICustomerDao {
  ISlice<CustomerPresenter> getCusomters(Pageable pageable, String name, String phone, String email);

  void updateCustomer(CustomerUpdate customer);

  Long addCustomer(CustomerAdd customer);

  void deleteCustomer(int customerId);
}
