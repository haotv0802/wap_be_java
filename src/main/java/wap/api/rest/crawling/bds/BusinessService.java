package wap.api.rest.crawling.bds;

import org.springframework.stereotype.Service;

@Service("bdsBusinessService")
public class BusinessService {

  public boolean isSale(String title) {
    title = title.toLowerCase();
    if (title.contains("can ban")
        || title.contains("can ho")
        || title.contains("chung cu")
        || title.contains("sang nhuong")
        || title.contains("suat noi bo")
        || title.contains("chuyen nhuong")
        || title.contains("gio hang")
        || title.contains("sang lai can")
        || title.contains("ki gui")
        || title.contains("dau tu")
        || title.contains("ban ch ")
        || title.contains("vinhomes")
        || title.contains("richstar")
        || title.contains("chu dau tu")
    ) {
     return true;
    }
    return false;
  }

  public boolean isSale(String name, String email) {
    name = name.toLowerCase();
    email = email.toLowerCase();
    if (name.contains("real")
        || name.contains("land")
        || name.contains("moi gioi")
        || name.contains("bds")
        || name.contains("bđs")
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
        || email.contains("business")
        || email.contains("canho")
        || email.contains("nhapho")
        || email.contains("masteri")
        || email.contains("sgdreamhouse")
        || email.contains("vietinhouse")
        || email.contains("nhathue")
        || email.contains("nhaban")
        || email.contains("thaodien")
        || email.contains("giahungphat")
        || email.contains("savills")
        || email.contains("datxanhgroup")
        || email.contains("khangdien")
        || email.contains("khaihoanland")
        || email.contains("gamuda")
        || email.contains("saigonreal.vn")
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
