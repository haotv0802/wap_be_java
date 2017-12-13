package wap.api.rest.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wap.api.rest.auth.beans.Credentials;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
@RestController("loginResource")
@RequestMapping(path = "/svc")
public class LoginResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @PostMapping("/login")
  public ResponseEntity login(
      @RequestBody Credentials credentials
  ) {
    if (credentials.getUsername().equals("haoho")) {
      return new ResponseEntity(HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }
  }
}