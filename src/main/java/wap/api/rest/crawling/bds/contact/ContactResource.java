package wap.api.rest.crawling.bds.contact;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.contact.beans.ContactPresenter;
import wap.api.rest.crawling.bds.contact.beans.ContactUpdate;
import wap.common.WapValidator;

import java.io.IOException;
import java.util.List;

/**
 * Created by haoho on 5/8/18 09:04.
 */
@RestController("bdsContactResource")
@RequestMapping(path = "/svc/bds/contact")
public class ContactResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  private final IContactService contactService;

  private final WapValidator<List<ContactUpdate>> contactUpdateValidator;

  @Autowired
  public ContactResource(
      @Qualifier("bdsContactService") IContactService contactService,
      @Qualifier("contactUpdateValidator") WapValidator<List<ContactUpdate>> contactUpdateValidator
  ) {
    Assert.notNull(contactService);
    Assert.notNull(contactUpdateValidator);

    this.contactService = contactService;
    this.contactUpdateValidator = contactUpdateValidator;
  }

  @GetMapping("/list")
  public ISlice<ContactPresenter> getContacts(
      Pageable pageable,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "phone", required = false) String phone,
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "manualCheck", required = false) String manualCheck,
      @RequestParam(value = "emailExisting", required = false) String emailExisting
      ){
    return this.contactService.getContacts(pageable, name, phone, email, type, manualCheck, emailExisting);
  }

  @PostMapping("/update")
  public ResponseEntity updateContacts(
      @RequestBody List<ContactUpdate> contacts
      ){

    this.contactUpdateValidator.validate(contacts);

    contactService.updateContacts(contacts);

    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/update/emailStatus/{status}/csv")
  public ResponseEntity updateEmailStatusWithCSV(
      @PathVariable("status") String status,
      @RequestParam("csvFile")MultipartFile csvFile
      ) throws IOException {

    this.contactService.updateEmailStatusWithCSV(csvFile.getInputStream(), status);
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }
}
