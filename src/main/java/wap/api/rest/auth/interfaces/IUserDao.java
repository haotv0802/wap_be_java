package wap.api.rest.auth.interfaces;

import wap.api.rest.auth.beans.User;

/**
 * Date: 12/14/2017
 *
 * @author haho
 */
public interface IUserDao {
  void signup(User user);

  boolean isUserExisting(String username);
}