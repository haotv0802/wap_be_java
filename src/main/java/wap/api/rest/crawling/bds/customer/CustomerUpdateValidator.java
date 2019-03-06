package wap.api.rest.crawling.bds.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.customer.beans.CustomerUpdate;
import wap.common.ValidationException;
import wap.common.WapValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haoho on 3/5/19 16:54.
 */
@Service("customerUpdateValidator")
public class CustomerUpdateValidator implements WapValidator<List<CustomerUpdate>> {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Override
  public String defaultFaultCode() {
    return "customer.add.invalid";
  }

  @Override
  public void validate(List<CustomerUpdate> customerUpdateList, String faultCode, Object... args) {
    Assert.notNull(customerUpdateList);

    for (CustomerUpdate customer: customerUpdateList) {
      Assert.notNull(customer);
      if (!customer.getUpdated()) {
        continue;
      }

      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();
      Set<ConstraintViolation<CustomerUpdate>> violations = validator.validate(customer);

      if (!violations.isEmpty()) {
        for (ConstraintViolation<CustomerUpdate> violation : violations) {
          String propertyPath = violation.getPropertyPath().toString();
          String message = violation.getMessage();
          throw new ValidationException("customer.update.constraintviolation", new String[]{propertyPath, message});
        }
      }

      String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

      Pattern pattern = Pattern.compile(regex);

      Matcher matcher = pattern.matcher(customer.getEmail());

      if (!matcher.matches()) {
        throw new ValidationException("customer.update.email.invalid");
      }
    }
  }
}
