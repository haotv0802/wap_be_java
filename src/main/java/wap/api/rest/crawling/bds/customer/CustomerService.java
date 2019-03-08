package wap.api.rest.crawling.bds.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.customer.beans.CustomerAdd;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;

import java.util.List;

/**
 * Created by haoho on 10/17/18 16:38.
 */
@Service("bdsCustomerService")
public class CustomerService implements ICustomerService {

  private final ICustomerDao customerDao;

  @Autowired
  public CustomerService(@Qualifier("bdsCustomerDao") ICustomerDao customerDao) {
    Assert.notNull(customerDao);

    this.customerDao = customerDao;
  }

  @Override
  public ISlice<CustomerPresenter> getCustomers(Pageable pageable, String name, String phone, String email) {

    return this.customerDao.getCustomers(pageable, name, phone, email);
  }

  @Override
  public void updateCustomers(List<CustomerPresenter> customers) {
    for(CustomerPresenter customer : customers) {
      if (customer.getUpdated()) {
        customerDao.updateCustomer(customer);
      }
    }
  }

  @Override
  public Long addCustomer(CustomerAdd customer) {
    return customerDao.addCustomer(customer);
  }

  @Override
  public void deleteCustomers(List<String> customers) {
    for(String id : customers) {
      int customerId = -1;
      try {
        customerId = Integer.parseInt(id);
      } catch (NumberFormatException ex) {
        ex.printStackTrace();
      }
      if (customerId != -1)
        customerDao.deleteCustomer(customerId);
    }
  }
}
