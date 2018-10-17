package wap.api.rest.crawling.bds.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;

/**
 * Created by haoho on 10/17/18 16:27.
 */
@RestController("bdsCustomerResource")
@RequestMapping(path = "/svc/bds/customer")
public class CustomerResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  final private ICustomerService customerService;

  @Autowired
  public CustomerResource(@Qualifier("bdsCustomerService") ICustomerService customerService) {
    Assert.notNull(customerService);

    this.customerService = customerService;
  }

  @GetMapping("/list")
  public ISlice<CustomerPresenter> getCustomers(
      Pageable pageable,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "phone", required = false) String phone,
      @RequestParam(value = "email", required = false) String email
  ){

    return this.customerService.getCustomers(pageable, name, phone, email);
  }
}
