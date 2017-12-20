package wap.api.rest.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.auth.beans.User;
import wap.api.rest.auth.interfaces.IUserDao;
import wap.api.rest.auth.interfaces.IUserService;

/**
 * Date: 12/14/2017
 *
 * @author haho
 */
@Service("userService")
public class UserService implements IUserService {

  private final IUserDao userDao;

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Autowired
  public UserService(@Qualifier("userDao") IUserDao userDao) {
    Assert.notNull(userDao);

    this.userDao = userDao;
  }

  @Override
  public void signup(User user) {
    userDao.signup(user);
  }

  @Override
  public Boolean isUserExisting(String username) {
    if (userDao.isUserExisting(username)) {
      return true;
    } else {
      return false;
    }
  }
}