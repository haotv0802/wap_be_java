package wap.api.rest.crawling.bds.customer.validators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.customer.ICustomerDao;
import wap.api.rest.crawling.bds.customer.beans.CustomerAdd;
import wap.common.ValidationException;
import wap.common.WapValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haoho on 3/1/19 10:37.
 */
@Service("customerAddValidator")
public class CustomerAddWapValidator implements WapValidator<CustomerAdd> {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  private final ICustomerDao customerDao;

  @Autowired
  public CustomerAddWapValidator(@Qualifier("bdsCustomerDao") ICustomerDao customerDao) {
    Assert.notNull(customerDao);

    this.customerDao = customerDao;
  }

  @Override
  public String defaultFaultCode() {
    return "customer.add.invalid";
  }

  @Override
  public void validate(CustomerAdd customer, String faultCode, Object... args) {
    Assert.notNull(customer);
//    Assert.notNull(customer.getEmail());
//    Assert.notNull(customer.getPhone());
//    Assert.notNull(customer.getName());

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<CustomerAdd>> violations = validator.validate(customer);

    if (!violations.isEmpty()) {
      for (ConstraintViolation<CustomerAdd> violation : violations) {
        String propertyPath = violation.getPropertyPath().toString();
        String message = violation.getMessage();
        throw new ValidationException("customer.add.constraintviolation", new String[]{propertyPath, message});
      }
    }


//    if (customer.getName().isEmpty()) {
//      throw new ValidationException("customer.add.name.notnull");
//    }
//    if (customer.getPhone().isEmpty()) {
//      throw new ValidationException("customer.add.phone.notnull");
//    }
//    if (customer.getEmail().isEmpty()) {
//      throw new ValidationException("customer.add.email.notnull");
//    }

    String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    Pattern pattern = Pattern.compile(regex);

    Matcher matcher = pattern.matcher(customer.getEmail());

    if (!matcher.matches()) {
      throw new ValidationException("customer.add.email.invalid");
    }

    if (customerDao.checkEmailExisting(customer.getEmail())) {
      throw new ValidationException("customer.add.email.existing");
    }

    if (customerDao.checkPhoneExisting(customer.getPhone())) {
      throw new ValidationException("customer.add.phone.existing");
    }
  }
}
