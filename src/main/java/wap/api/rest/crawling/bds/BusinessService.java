package wap.api.rest.crawling.bds;

import org.springframework.stereotype.Service;

@Service("bdsBusinessService")
public class BusinessService {
  public boolean isSale(String name, String email) {
    name = name.toLowerCase();
    email = email.toLowerCase();
    if (name.contains("real")
        || name.contains("land")
        || name.contains("bds")
        || name.contains("sale")
        || name.contains("team")
        || name.contains("celadon")
        || name.contains("kinh doanh")
        || name.contains("giao dich")
        || name.contains("bat đong san")
        ) {
      return true;
    }
    if (email.contains("bds")
        || email.contains("batdongsan")
        || email.contains("real")
        || email.contains("diaocsaigon")
        || email.contains("nhadat")
        || email.contains("saigonhousecenter")
        || email.contains("saigoncenterreal")
        || email.contains("land")
        || email.contains("anhkhoiviettin")
        || email.contains("vinhomes")
        || email.contains("richgroup")
        || email.contains("sale")
        || email.contains("celadon")
        ) {
      return true;
    }
    return false;
  }
}
