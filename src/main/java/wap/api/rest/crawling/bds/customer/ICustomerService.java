package wap.api.rest.crawling.bds.customer;

import org.springframework.data.domain.Pageable;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.customer.beans.CustomerAdd;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;

import java.util.List;

/**
 * Created by haoho on 10/17/18 16:37.
 */
public interface ICustomerService {
  ISlice<CustomerPresenter> getCustomers(Pageable pageable, String name, String phone, String email);

  void updateCustomers(List<CustomerPresenter> customers);

  Long addCustomer(CustomerAdd customer);

  void deleteCustomers(List<String> customers);
}
