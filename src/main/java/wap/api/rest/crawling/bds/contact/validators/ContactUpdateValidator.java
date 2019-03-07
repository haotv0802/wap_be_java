package wap.api.rest.crawling.bds.contact.validators;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.contact.beans.ContactUpdate;
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
 * Created by haoho on 3/7/19 10:39.
 */
@Service("contactUpdateValidator")
public class ContactUpdateValidator implements WapValidator<List<ContactUpdate>> {
  @Override
  public String defaultFaultCode() {
    return "contact.update.invalid";
  }

  @Override
  public void validate(List<ContactUpdate> contactUpdateList, String faultCode, Object... args) {
    Assert.notNull(contactUpdateList);

    for(ContactUpdate contact : contactUpdateList) {
      if (!contact.getUpdated()) {
        continue;
      }

      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();
      Set<ConstraintViolation<ContactUpdate>> violations = validator.validate(contact);

      if (!violations.isEmpty()) {
        for (ConstraintViolation<ContactUpdate> violation : violations) {
          String propertyPath = violation.getPropertyPath().toString();
          String message = violation.getMessage();
          throw new ValidationException("contact.update.constraintviolation", new String[]{propertyPath, message});
        }
      }

      String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

      Pattern pattern = Pattern.compile(regex);

      Matcher matcher = pattern.matcher(contact.getEmail());

      if (!matcher.matches()) {
        throw new ValidationException("contact.update.email.invalid");
      }
    }
  }
}
