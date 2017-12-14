package wap.api.rest.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wap.api.rest.auth.beans.User;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
@RestController("userResource")
@RequestMapping(path = "/svc")
public class UserResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @PostMapping("/signup")
  public ResponseEntity signup(
      @RequestBody User user
  ) {
    return new ResponseEntity(HttpStatus.OK);
  }
}