package wap.api.rest.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.auth.beans.Credentials;
import wap.api.rest.auth.interfaces.ILoginDao;
import wap.api.rest.auth.interfaces.ILoginService;

/**
 * Date: 12/14/2017
 *
 * @author haho
 */
@Service("loginService")
public class LoginService implements ILoginService {

  private final ILoginDao loginDao;

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Autowired
  public LoginService(@Qualifier("loginDao") ILoginDao loginDao) {
    Assert.notNull(loginDao);

    this.loginDao = loginDao;
  }

  @Override
  public boolean login(Credentials credentials) {
    return loginDao.login(credentials);
  }
}