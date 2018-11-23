package wap.api.rest.crawling.bds;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service("bdsBusinessService")
public class BusinessService {

  public boolean isSale(String title) {
    title = title.toLowerCase();
    title = StringUtils.stripAccents(title);
    if (   title.contains("suat noi bo")
        || title.contains("chuyen nhuong")
        || title.contains("gio hang")
        || title.contains("sang lai can")
    ) {
     return true;
    }
    return false;
  }

  public boolean isSale(String name, String email) {
    name = name.toLowerCase();
    name = StringUtils.stripAccents(name);

    email = email.toLowerCase();
    email = StringUtils.stripAccents(email);
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
    String type = StringUtils.stripAccents(businessType.toLowerCase());
    if (type.equalsIgnoreCase("ban")) {
      return "SELLING";
    } else if (type.equalsIgnoreCase("cho thue")) {
      return "FOR RENT";
    } else if (type.equalsIgnoreCase("can thue")) {
      return "RENT";
    } else if (type.equalsIgnoreCase("mua")) {
      return "BUYING";
    } else {
      return null;
    }
  }

  public String getPropertyType(String propertyType) {
    String type = StringUtils.stripAccents(propertyType.toLowerCase());
    if (type.equalsIgnoreCase("nha rieng")) {
      return "HOUSE";
    } else if (type.equalsIgnoreCase("can ho chung cu")) {
      return "APARTMENT";
    } else if (type.equalsIgnoreCase("nha mat pho")) {
      return "BIG HOUSE";
    } else if (type.equalsIgnoreCase("nha biet thu, lien ke")) {
      return "VILLA";
    } else if (type.equalsIgnoreCase("dat nen du an") || type.equalsIgnoreCase("đat nen du an")) {
      return "PROJECT LAND";
    } else if (type.equalsIgnoreCase("dat") || type.equalsIgnoreCase("đat")) {
      return "LAND";
    } else if (type.equalsIgnoreCase("trang trai, khu nghi duong")) {
      return "FARM, RESORT";
    } else if (type.equalsIgnoreCase("kho, nha xuong")) {
      return "WAREHOUSE";
    } else if (type.equalsIgnoreCase("loai bat dong san khac") || type.equalsIgnoreCase("loai bat đong san khac")) {
      return "OTHER";
    } else {
      return null;
    }
  }
}
