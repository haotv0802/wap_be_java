package wap.api.rest.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wap.api.rest.auth.beans.Credentials;
import wap.api.rest.auth.interfaces.ILoginService;

/**
 * Date: 12/14/2017
 *
 * @author haho
 */
@RestController("loginResource")
@RequestMapping(path = "/svc")
public class LoginResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  private final ILoginService loginService;

  @Autowired
  public LoginResource(@Qualifier("loginDao") ILoginService loginService) {
    Assert.notNull(loginService);

    this.loginService = loginService;
  }

  @PostMapping("/login")
  public ResponseEntity login(
      @RequestBody Credentials credentials
  ) {
    if (loginService.login(credentials)) {
      return new ResponseEntity(HttpStatus.OK);
    } else {
      return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }
  }
}