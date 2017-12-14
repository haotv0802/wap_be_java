package wap.api.rest.auth.interfaces;

import wap.api.rest.auth.beans.Credentials;

/**
 * Date: 12/14/2017
 *
 * @author haho
 */
public interface ILoginService {
  boolean login(Credentials credentials);
}