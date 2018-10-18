package wap.api.rest.crawling.bds.customer;

import org.springframework.data.domain.Pageable;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;

/**
 * Created by haoho on 10/17/18 16:48.
 */
public interface ICustomerDao {
  ISlice<CustomerPresenter> getCusomters(Pageable pageable, String name, String phone, String email);

  void updateCustomer(CustomerPresenter customer);

  Long addCustomer(CustomerPresenter customer);
}
