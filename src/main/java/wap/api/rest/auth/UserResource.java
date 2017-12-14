package wap.api.rest.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wap.api.rest.auth.beans.User;
import wap.api.rest.auth.interfaces.IUserService;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 *
 * @author haho
 */
@RestController("userResource")
@RequestMapping(path = "/svc")
public class UserResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  private final IUserService userService;

  public UserResource(@Qualifier("userService") IUserService userService) {
    org.springframework.util.Assert.notNull(userService);

    this.userService = userService;
  }

  @PostMapping("/signup")
  public ResponseEntity signup(
      @RequestBody User user
  ) {
    userService.signup(user);
    return new ResponseEntity(HttpStatus.OK);
  }
}