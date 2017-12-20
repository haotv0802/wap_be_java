package wap.api.rest.auth.interfaces;

import wap.api.rest.auth.beans.User;

/**
 * Date: 12/14/2017
 *
 * @author haho
 */
public interface IUserService {
  void signup(User user);
  Boolean isUserExisting(String username);
}