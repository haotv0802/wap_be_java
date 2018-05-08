package wap.api.rest.crawling.bds.email;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 12/21/2017
 *
 * @author haho
 */
@Service("bdsEmailAccountService")
public class EmailAccountService implements IEmailAccountService {

  @Override
  public Map<String, String> getEmailsInfo() {

    Map<String, String> emailAccounts = new HashMap<>();
    emailAccounts.put("vanthuc82@gmail.com", "ljkcsnbudkviblaz");
//    emailAccounts.put("nguyenphuc130905@gmail.com", "juwghvjihdiewqlw");
//    emailAccounts.put("quangcaobds180303@gmail.com", "gwswsjdfidpnbpge");
//    emailAccounts.put("quangcaobds180304@gmail.com", "rupkcdyagxzmsopm");
//    emailAccounts.put("quangcaobds180300@gmail.com", "ovdnnklpkxwxeocr");
//    emailAccounts.put("quangcaobds180301@gmail.com", "xrztwxsjasjzjayg");
//    emailAccounts.put("quangcaobds180302@gmail.com", "xuyaxdhgmuofgyfo");

    return emailAccounts;
  }
}