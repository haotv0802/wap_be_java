package wap.api.rest.crawling.bds.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.customer.beans.CustomerAdd;
import wap.common.ValidationException;
import wap.common.Validator;

/**
 * Created by haoho on 3/1/19 10:37.
 */
@Service("customerAddValidator")
public class CustomerAddValidator implements Validator<CustomerAdd> {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Override
  public String defaultFaultCode() {
    return "customerAddValidator";
  }

  @Override
  public void validate(CustomerAdd customer, String faultCode, Object... args) {
    Assert.notNull(customer);
    Assert.notNull(customer.getEmail());

    if (customer.getEmail().isEmpty()) {
      throw new ValidationException("expense.userId.invalid");
    }
  }
}
