package wap.api.rest.crawling.bds.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.customer.beans.CustomerAdd;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;
import wap.api.rest.crawling.bds.customer.beans.CustomerUpdate;
import wap.common.WapValidator;

import java.util.List;

/**
 * Created by haoho on 10/17/18 16:27.
 */
@RestController("bdsCustomerResource")
@RequestMapping(path = "/svc/bds/customer")
public class CustomerResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  final private ICustomerService customerService;

  final private WapValidator<CustomerAdd> customerAddWapValidator;

  final private WapValidator<List<CustomerUpdate>> customerUpdateWapValidator;

  @Autowired
  public CustomerResource(
      @Qualifier("bdsCustomerService") ICustomerService customerService,
      @Qualifier("customerAddValidator") WapValidator<CustomerAdd> customerAddWapValidator,
      @Qualifier("customerUpdateValidator") WapValidator<List<CustomerUpdate>> customerUpdateWapValidator
  ) {
    Assert.notNull(customerService);
    Assert.notNull(customerAddWapValidator);
    Assert.notNull(customerUpdateWapValidator);

    this.customerService = customerService;
    this.customerAddWapValidator = customerAddWapValidator;
    this.customerUpdateWapValidator = customerUpdateWapValidator;
  }

  @GetMapping("/list")
  public ISlice<CustomerPresenter> getCustomers(
      Pageable pageable,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "phone", required = false) String phone,
      @RequestParam(value = "email", required = false) String email
  ) {

    return this.customerService.getCustomers(pageable, name, phone, email);
  }


  @PostMapping("/update")
  public ResponseEntity updateCustomers(
      @RequestBody List<CustomerUpdate> customers
  ) {

    this.customerUpdateWapValidator.validate(customers);

    customerService.updateCustomers(customers);

    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/add")
  public ResponseEntity addCustomer(
      @RequestBody CustomerAdd customer
  ) {

    this.customerAddWapValidator.validate(customer);

    Long id = customerService.addCustomer(customer);

    return new ResponseEntity<>(id, HttpStatus.CREATED);
  }

  @PostMapping("/delete")
  public ResponseEntity deleteCustomers(
      @RequestBody List<String> customers
  ) {
    customerService.deleteCustomers(customers);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
