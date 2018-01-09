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
        || name.contains("đau tu")
        || name.contains("giao dich")
        || name.contains("bat đong san")
        || name.contains("mua ban")
        || name.contains("muaban")
        || name.contains("tai san")
        || name.contains("cong ty")
        || name.contains("ktts")
        || name.contains("cskh")
        || name.contains("truhomes")
        ) {
      return true;
    }
    if (email.contains("bds")
        || email.contains("batdongsan")
        || email.contains("diaoc")
        || email.contains("real")
        || email.contains("chungcu")
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
        || email.contains("muaban")
        || email.contains("taisan")
        || email.contains("giaodich")
        || email.contains("cskh")
        || email.contains("truhomes")
        ) {
      return true;
    }
    return false;
  }

  public String getBusinessType(String businessType) {
    if (businessType.equalsIgnoreCase("ban")) {
      return "SELLING";
    } else if (businessType.equalsIgnoreCase("cho thue")) {
      return "FOR RENT";
    } else if (businessType.equalsIgnoreCase("can thue")) {
      return "RENT";
    } else if (businessType.equalsIgnoreCase("mua")) {
      return "BUYING";
    } else {
      return null;
    }
  }

  public String getPropertyType(String propertyType) {
    if (propertyType.equalsIgnoreCase("nha rieng")) {
      return "HOUSE";
    } else if (propertyType.equalsIgnoreCase("can ho chung cu")) {
      return "APARTMENT";
    } else if (propertyType.equalsIgnoreCase("nha mat pho")) {
      return "BIG HOUSE";
    } else if (propertyType.equalsIgnoreCase("nha biet thu, lien ke")) {
      return "VILLA";
    } else if (propertyType.equalsIgnoreCase("dat nen du an") || propertyType.equalsIgnoreCase("đat nen du an")) {
      return "PROJECT LAND";
    } else if (propertyType.equalsIgnoreCase("dat") || propertyType.equalsIgnoreCase("đat")) {
      return "LAND";
    } else if (propertyType.equalsIgnoreCase("trang trai, khu nghi duong")) {
      return "FARM, RESORT";
    } else if (propertyType.equalsIgnoreCase("kho, nha xuong")) {
      return "WAREHOUSE";
    } else if (propertyType.equalsIgnoreCase("loai bat dong san khac") || propertyType.equalsIgnoreCase("loai bat đong san khac")) {
      return "OTHER";
    } else {
      return null;
    }
  }
}
