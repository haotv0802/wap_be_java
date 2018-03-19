package wap.api.rest.crawling.bds;

import com.sun.mail.smtp.SMTPSendFailedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.beans.*;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataDao;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataService;
import wap.api.rest.crawling.bds.interfaces.IEmailAccountService;
import wap.api.rest.crawling.mailing.JavaMailService;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Date: 10/20/2017 Time: 10:19 AM
 *
 * @author haho
 */
@Repository("bdsCrawledDataService")
public class CrawledDataService implements ICrawledDataService {
  private final ICrawledDataDao crawledDataDao;

  private final BusinessService businessService;

  private final IEmailAccountService emailAccountService;

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Autowired
  public CrawledDataService(
      @Qualifier("bdsCrawledDataDao") ICrawledDataDao crawledDataDao,
      @Qualifier("bdsBusinessService") BusinessService businessService,
      @Qualifier("bdsEmailAccountService") IEmailAccountService emailAccountService) {

    Assert.notNull(crawledDataDao);
    Assert.notNull(businessService);
    Assert.notNull(emailAccountService);

    this.emailAccountService = emailAccountService;
    this.crawledDataDao = crawledDataDao;
    this.businessService = businessService;
  }

  @Override
  public List<ItemPresenter> getAllItems() {
    return this.crawledDataDao.getAllItems();
  }

  @Override
  public List<ItemPresenter> getAllItemsByCriterion(Criterion criterion) {
    return this.crawledDataDao.getAllItemsByCriterion(criterion);
  }

  @Override
  public List<ItemPresenter> exportCrawledData(Criterion criterion) throws IOException {
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet("Contacts");
    sheet.autoSizeColumn(0);
    sheet.setColumnWidth(1, 5000);
    sheet.setColumnWidth(2, 3000);
    sheet.setColumnWidth(3, 7000);
    sheet.setColumnWidth(4, 4000);
    sheet.setColumnWidth(5, 5000);
    sheet.setColumnWidth(6, 4000);
    sheet.setColumnWidth(7, 4000);
    sheet.createFreezePane(0, 1);
//    sheet.setDefaultColumnWidth(20);

    CellStyle headerStyle = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
    font.setFontHeight(18);

    headerStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
    headerStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
    headerStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
    headerStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
    headerStyle.setFillForegroundColor((short) 200);
    headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
    headerStyle.setFont(font);


    CreationHelper createHelper = workbook.getCreationHelper();
    XSSFCellStyle hlinkstyle = workbook.createCellStyle();
    XSSFFont hlinkfont = workbook.createFont();
    hlinkfont.setUnderline(XSSFFont.U_SINGLE);
    hlinkfont.setColor(HSSFColor.BLUE.index);
    hlinkstyle.setFont(hlinkfont);

    int rowCount = 0;
    int columnCount = 0;

    Row row = sheet.createRow(rowCount);
    Cell cell = row.createCell(++columnCount);
    cell.setCellValue("Tên");
    cell.setCellStyle(headerStyle);

    cell = row.createCell(++columnCount);
    cell.setCellValue("SDT");
    cell.setCellStyle(headerStyle);

    cell = row.createCell(++columnCount);
    cell.setCellValue("Email");
    cell.setCellStyle(headerStyle);

    cell = row.createCell(++columnCount);
    cell.setCellValue("Giá (*1000)");
    cell.setCellStyle(headerStyle);

    cell = row.createCell(++columnCount);
    cell.setCellValue("Dien tich (m2)");
    cell.setCellStyle(headerStyle);

    cell = row.createCell(++columnCount);
    cell.setCellValue("Thành phố");
    cell.setCellStyle(headerStyle);

    cell = row.createCell(++columnCount);
    cell.setCellValue("Quận");
    cell.setCellStyle(headerStyle);

    cell = row.createCell(++columnCount);
    cell.setCellValue("Link");
    cell.setCellStyle(headerStyle);

    List<ItemPresenter> list = crawledDataDao.getAllItemsByCriterion(criterion);

    for (ItemPresenter item : list) {
      String email = item.getContactEmail().toLowerCase();
      if (   email.contains("bds")
          || email.contains("batdongsan")
          || email.contains("real")
          || email.contains("diaocsaigon")
          || email.contains("nhadat")
          || email.contains("saigonhousecenter")
          || email.contains("saigoncenterreal")
          || email.contains("land")
          || email.contains("anhkhoiviettin")
          || email.contains("vinhomes")
          || email.contains("richgroup")) {
        continue;
      }

      String name = item.getContactName().toLowerCase();
      if (name.contains("real")
        || name.contains("land")
          || name.contains("bds")
          || name.contains("bất động sản")
          ) {
        continue;
      }

      row = sheet.createRow(++rowCount);

      columnCount = 0;
      cell = row.createCell(columnCount);
      cell.setCellValue(rowCount);
      cell = row.createCell(++columnCount);
      cell.setCellValue(item.getContactName());
      cell = row.createCell(++columnCount);
      cell.setCellValue(item.getContactNumber());
      cell = row.createCell(++columnCount);
      cell.setCellValue(item.getContactEmail());

      cell = row.createCell(++columnCount);
      NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
      usdCostFormat.setMinimumFractionDigits( 1 );
      usdCostFormat.setMaximumFractionDigits( 2 );

//      cell.setCellValue(item.getPrice().toString());
      cell.setCellValue(usdCostFormat.format(item.getPrice()));

      cell = row.createCell(++columnCount);
      cell.setCellValue(item.getAcreage().toString());
      cell = row.createCell(++columnCount);
      cell.setCellValue(item.getCity());
      cell = row.createCell(++columnCount);
      cell.setCellValue(item.getDistrict());
      cell = row.createCell(++columnCount);
      XSSFHyperlink link = (XSSFHyperlink)createHelper.createHyperlink(Hyperlink.LINK_URL);
      link.setAddress(item.getUrl());
      cell.setCellValue(item.getUrl());
      cell.setHyperlink((XSSFHyperlink) link);
      cell.setCellStyle(hlinkstyle);
    }

    File dir = new File ("exports");
    if (!dir.exists() || !dir.isDirectory()) {
      dir.mkdir();
    }
    SimpleDateFormat spd = new SimpleDateFormat("yyyyMMdd_hhmmss");
    String date = spd.format(new Date());
    try (FileOutputStream outputStream = new FileOutputStream(String.format("%s/%s_%s.xlsx", dir.getName(), criterion.getPersonName(), date))) {
      workbook.write(outputStream);
    }
    return list;
  }

  @Override
  public List<String> getEmails() {
    return this.crawledDataDao.getEmails();
  }

  @Override
  public void sendAdsToCustomers() throws MessagingException {

//    List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(14);
//    List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(7);
//    List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(9);
//    List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(16);
//      List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(23);
//    List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(17);
//    List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(10);
//    List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(4);
    List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(22);
//    List<String> temps = new ArrayList<>();
//    temps.add("hoanhhao@gmail.com");
//    temps.add("nguyenminhyennhi94@gmail.com");

//    String from = "yennhi.gamuda@gmail.com";
//    String fromPass = "hpzitbyzeslypetm";

    String from = "vanthuc82@gmail.com";
    String fromPass = "ljkcsnbudkviblaz";
//quangcaobds180300@gmail.com
//    ovdnnklpkxwxeocr

    String title = "XUÂN SANG HÁI LỘC LÊN ĐẾN NỬA TỶ ĐỒNG !!!";

    String emailBody = "Xin chào Anh/Chị, <br/> "
        + "GAMUDA LAND triển khai chương trình “XUÂN SANG HÁI LỘC” nhằm tri ân khách hàng. <br><br>"

        + "<img src=\"https://lh3.googleusercontent.com/OEMTb7gnnFdaySb4BLARZVfbRmy8JsCPEIdQMLN1FIeeHqdMHdNsopviRLFLf3Y7XVnxSz_mB-PQ2D3GWhhko7cvCW6-Y2WdeObj5wUplrDTQBvafpyAegWo2IDKIaaw0PQazy1X9GvVZpYp1e7ODFPTTBLnv3KI6Scq_xhf_advrAsavuDMymo8oUsfzErFqrLZduBmrsQ6onTtvVeuxPnecDSunO02XxCVQ-K1Bn5VlDnODN_OPwkZ0Bcyxzto_Y9tWd3j0h99QwXqXkD_InTrXr3uzj0ktWR0CVgaldCTgUH0F-OlF-lKP23zGucIP_kISV8NVaW0Y4L5hfbCLd_iqm90-EMG3sa-voia5wnkM00ad0H8WCIJLwhOF_k2JWfhLzkIlUq_rXn9nGLYIwm9ATFzc6bH99ZLFjtrBnPYWzyu9Jwap_xiVJ1JcmeKP_oaHAoS1dzOEa48FWuNUmsX2R89_1c5uFQQ87NQhm6DkAMEshv7wLO8oLSBOzg8mU00g-Hi4_5pUqEWmgAL_e-mNJsdrRyZS5fZD7Bqi_m2lO7j1EHNdmBfF3ghEOMQUlqSioM5tUA6cruw3DgMFktv0hcHo8fIL6tIXh2l=w961-h503-no\">"
        + "Khi quý vị sở hữu 1 căn hộ sẽ có cơ hội hái được 1 lộc có trị giá từ 200tr đến 500tr <br><br>"
        + "  Hãy để GAMUDA LAND gửi đến quý khách niềm vui trọn vẹn hơn trong dịp tết này khi có trong tay căn hộ với những tiện ích,thiết kế nội thất thông minh sang trọng và trải nghiệm cuộc sống đẳng cấp nơi khu ĐÔ THỊ XANH CELEDON CITY mang lại. <br><br>"
        + "  Trong trường hợp quý vị có nhu cầu sở hữu căn hộ để ở hoặc đầu tư vui lòng liên hệ thông tin sau: <br><br>"
        + "   Điện thoại: 0906996169 <br>"
        + "   Email: "
        + "yennhi.gamuda@gmail.com <br>"
        + "Kính chúc khách hàng 1 năm mới với nhiều niềm vui và may mắn."
        + "<br><br> Trân trọng, <br>";

    for (ContactPresenter contactPresenter : list) {
      System.out.println(">>>>> " + contactPresenter.getEmail());
      if (!this.crawledDataDao.checkEmailSentOrNot(from, contactPresenter.getEmail())) {
        JavaMailService.sendAdsToCustomer(from, fromPass, contactPresenter.getEmail(), title, emailBody);
        this.crawledDataDao.trackEmailSent(from, contactPresenter.getEmail(), title, emailBody);
        this.crawledDataDao.updateEmailExisting(contactPresenter.getEmail(), true);
      }
    }
  }


  @Override
  public void sendAdsBlockEToCustomers() throws MessagingException {

    List<ContactPresenter> list = new ArrayList<>();
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(14)); // Tan Binh
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(7)); // Tan Phu
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(9)); // Thu Duc
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(16)); // Quan 7
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(23)); // Quan 2
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(17)); // Quan 1
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(10)); // Binh Tan
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(4)); // Phu Nhuan
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(22)); // Quan 3
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(5)); // Quan 10
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(3)); // Quan Go Vap
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(6)); // Quan 4
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(1)); // Quan 12

//    List<ContactPresenter> list = new ArrayList<>();
//    ContactPresenter cp = new ContactPresenter();
//    cp.setEmail("hoanhhao@gmail.com");
//    list.add(cp);
//    cp = new ContactPresenter();
//    cp.setEmail("nguyenminhyennhi94@gmail.com");
//    list.add(cp);

//    String from = "yennhi.gamuda@gmail.com";
//    String fromPass = "hpzitbyzeslypetm";

//    String from = "quangcaobds180300@gmail.com";
//    String fromPass = "ovdnnklpkxwxeocr";

//    String from = "quangcaobds180301@gmail.com";
//    String fromPass = "xrztwxsjasjzjayg";

    Map<String, String> emailAccounts = emailAccountService.getEmailsInfo();


    Set<String> keys = emailAccounts.keySet();
    Iterator<String> iterator = keys.iterator();

//    String title = "Ưu đãi cực lớn Block cuối cùng và đẹp nhất khu EMERALD dự án Celadon city !!!";
    String title = "Cơ hội cuối cùng sở hữu căn hộ khu Emerald tại Celadon City (AEON Mall Tân Phú) !!!";

    StringBuilder email = new StringBuilder();
    email.append("<img width='100%'  src=\"https://i.imgur.com/8x50AC8.png\"><br>");
    email.append("Xin chào Anh/Chị, <br/> ");
    email.append("Celadon city thông báo đến quý khách hàng chương trình bán hàng block E ngày 18/3/2018; là block cuối cùng của Emerald. <br><br>");
    email.append("****Nhận giữ chổ ngày 10/3/2018, 50tr/ giữ chổ.<br>");
    email.append("** Chiếc khấu : 5% trên giá bán.<br>");
    email.append("** Kết nối cộng đồng: ck 1%<br>");
    email.append("** Tri ân khách hàng: ck 1%<br>");
    email.append("** Voucher mừng xuân: 25tr<br>");
    email.append("** Gói chăm sóc sức khoẻ Hoàn Mỹ cho gia đình trong 1 năm.<br>");
    email.append("** Bốc thăm iphone X, ipad, note 8<br>");
    email.append("Quý khách hàng đăng ký tham gia chương trình vui lòng liên hệ em book ngay cho PKD nhé vì số lượng có hạn ạ!<br>");
    email.append("<b>Hotline: 0906 99 61 69 (viber/zalo)</b><br>");
    email.append("   Email: yennhi.gamudaland@gmail.com <br>");
    email.append("*** Phương thức thanh toán :<br>");
    email.append("- Ký hợp đồng 15%<br>");
    email.append("- 4 tháng sau 15%<br>");
    email.append("- Cất nóc 15%<br>");
    email.append("- Nhận nhà 30%<br>");
    email.append("- Sau 3 tháng nhận nhà 20%<br>");
    email.append("- Cấp sổ 5%<br>");
    email.append("Trân trọng!<br>");
    email.append("<img width='100%' src=\"https://i.imgur.com/PGMqVRD.jpg\"><br>");
    email.append("<img width='100%'  src=\"https://i.imgur.com/yIDaoIH.jpg\"><br>");
    email.append("<img width='100%'  src=\"https://i.imgur.com/yFwry1g.jpg\"><br>");
    email.append("<img width='100%'  src=\"https://i.imgur.com/FaI82Sm.jpg\"><br>");
    email.append("<img width='100%'  src=\"https://i.imgur.com/8vkSUY2.jpg\"><br>");
    email.append("<img width='100%'  src=\"https://i.imgur.com/xkg4F7D.png\"><br>");

//    String title = "Cơ hội cuối cùng sở hữu căn hộ khu Emerald tại Celadon City (AEON Mall Tân Phú) !!!";
//
//    StringBuilder email = new StringBuilder();
//
//    email.append("<img width='100%' src=\"https://photos-6.dropbox.com/t/2/AABz1fl_uDFmBd6QjhupOGZuAhwIm0VeiKe7qFAI_MzJlw/12/64513069/png/32x32/3/1520931600/0/2/28424938_582155312127136_3338800246953675692_o.png/EM6lhTIYxogPIAIoAg/H3s87-nPMGhlZSV1M8lSlb93c8Fj9HY8ee8rrHAdjW8?dl=0&preserve_transparency=1&size=2048x1536&size_mode=3\"><br>");
//
//    email.append("Xin chào Anh/Chị, <br/> ");
//    email.append("Celadon city thông báo đến quý khách hàng chương trình bán hàng block E ngày 18/3/2018; là block cuối cùng của Emerald. <br><br>");
//    email.append("****Nhận giữ chổ ngày 10/3/2018, 50tr/ giữ chổ.<br>");
//    email.append("** Chiếc khấu : 5% trên giá bán.<br>");
//    email.append("** Kết nối cộng đồng: ck 1%<br>");
//    email.append("** Tri ân khách hàng: ck 1%<br>");
//    email.append("** Voucher mừng xuân: 25tr<br>");
//    email.append("** Gói chăm sóc sức khoẻ Hoàn Mỹ cho gia đình trong 1 năm.<br>");
//    email.append("** Bốc thăm iphone X, ipad, note 8<br>");
//    email.append("Quý khách hàng đăng ký tham gia chương trình báo em book ngay cho Pkd nhé vì số lượng có hạn ạ!<br>");
//    email.append("<b>Hotline: 0906 99 61 69 (viber/zalo)</b><br>");
//    email.append("   Email: yennhi.gamudaland@gmail.com <br>");
//    email.append("*** Phương thức thanh toán :<br>");
//    email.append("- Ký hợp đồng 15%<br>");
//    email.append("- 4 tháng sau 15%<br>");
//    email.append("- Cất nóc 15%<br>");
//    email.append("- Nhận nhà 30%<br>");
//    email.append("- Sau 3 tháng nhận nhà 20%<br>");
//    email.append("- Cấp sổ 5%<br>");
//    email.append("Trân trọng!<br>");

    String from;
    String fromPass;
    if (iterator.hasNext()) {
      from = iterator.next();
      fromPass = emailAccounts.get(from);

      for (ContactPresenter contactPresenter : list) {
        System.out.println("..... " + from + "  >>>>> " + contactPresenter.getEmail());
        if (!this.crawledDataDao.checkEmailSentOrNotWithTitle(title, contactPresenter.getEmail())) {
          try {
            JavaMailService.sendAdsToCustomer(from, fromPass, contactPresenter.getEmail(), title, email.toString());
            this.crawledDataDao.trackEmailSent(from, contactPresenter.getEmail(), title, email.toString());
            this.crawledDataDao.updateEmailExisting(contactPresenter.getEmail(), true);
          } catch (SMTPSendFailedException ex) {
            ex.printStackTrace();
            if (iterator.hasNext()) {
              from = iterator.next();
              fromPass = emailAccounts.get(from);
            } else {
              break;
            }
          }
        }
      }

    }

  }

  @Override
  public List<ContactPresenter> getPostsManually(Criterion criterion) throws IOException {

    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet("Contacts");
    sheet.autoSizeColumn(0);
    sheet.setColumnWidth(1, 5000);
    sheet.setColumnWidth(2, 3000);
    sheet.setColumnWidth(3, 7000);
    sheet.setColumnWidth(4, 4000);
    sheet.setColumnWidth(5, 5000);
    sheet.setColumnWidth(6, 4000);
    sheet.setColumnWidth(7, 4000);
    sheet.createFreezePane(0, 1);
//    sheet.setDefaultColumnWidth(20);

    CellStyle headerStyle = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
    font.setFontHeight(18);

    headerStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
    headerStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
    headerStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
    headerStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
    headerStyle.setFillForegroundColor((short) 200);
    headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
    headerStyle.setFont(font);


    CreationHelper createHelper = workbook.getCreationHelper();
    XSSFCellStyle hlinkstyle = workbook.createCellStyle();
    XSSFFont hlinkfont = workbook.createFont();
    hlinkfont.setUnderline(XSSFFont.U_SINGLE);
    hlinkfont.setColor(HSSFColor.BLUE.index);
    hlinkstyle.setFont(hlinkfont);

    int rowCount = 0;
    int columnCount = 0;

    Row row = sheet.createRow(rowCount);
    Cell cell = row.createCell(++columnCount);
    cell.setCellValue("Tên");
    cell.setCellStyle(headerStyle);

    cell = row.createCell(++columnCount);
    cell.setCellValue("SDT");
    cell.setCellStyle(headerStyle);

    cell = row.createCell(++columnCount);
    cell.setCellValue("Email");
    cell.setCellStyle(headerStyle);

    List<ContactPresenter> list = crawledDataDao.getPostsManually();

    for (ContactPresenter contact : list) {
      String email = contact.getEmail().toLowerCase();
      if (   email.contains("bds")
          || email.contains("batdongsan")
          || email.contains("real")
          || email.contains("diaocsaigon")
          || email.contains("nhadat")
          || email.contains("saigonhousecenter")
          || email.contains("saigoncenterreal")
          || email.contains("land")
          || email.contains("anhkhoiviettin")
          || email.contains("vinhomes")
          || email.contains("richgroup")) {
        continue;
      }

      String name = contact.getName().toLowerCase();
      if (name.contains("real")
          || name.contains("land")
          || name.contains("bds")
          || name.contains("bất động sản")
          ) {
        continue;
      }

      row = sheet.createRow(++rowCount);

      columnCount = 0;
      cell = row.createCell(columnCount);
      cell.setCellValue(rowCount);
      cell = row.createCell(++columnCount);
      cell.setCellValue(contact.getName());
      cell = row.createCell(++columnCount);
      cell.setCellValue(contact.getPhone());
      cell = row.createCell(++columnCount);
      cell.setCellValue(contact.getEmail());
    }

    File dir = new File ("exports");
    if (!dir.exists() || !dir.isDirectory()) {
      dir.mkdir();
    }
    SimpleDateFormat spd = new SimpleDateFormat("yyyyMMdd_hhmmss");
    String date = spd.format(new Date());
    try (FileOutputStream outputStream = new FileOutputStream(String.format("%s/%s_%s.xlsx", dir.getName(), criterion.getPersonName(), date))) {
      workbook.write(outputStream);
    }
    return list;
  }

  @Override
  public List<Contact> getContacts() {
    return this.crawledDataDao.getContacts();
  }

  @Override
  public void exportOwnerContacts() throws IOException {
    List<LocationPresenter> locations = this.crawledDataDao.getAllLocations();
    XSSFWorkbook workbook = new XSSFWorkbook();

    Map<String, Integer> summary = new HashMap<>();

    int total = 0;
    for (LocationPresenter location : locations) {
      List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(location.getId());

      if (list.size() == 0) {
        continue;
      }
      total += list.size();
      summary.put(location.getDistrict(), list.size());

      XSSFSheet sheet = workbook.createSheet(location.getDistrict() + " (" + list.size() + ")");
      sheet.autoSizeColumn(0);
      sheet.setColumnWidth(1, 5000);
      sheet.setColumnWidth(2, 3000);
      sheet.setColumnWidth(3, 7000);
      sheet.setColumnWidth(4, 4000);
      sheet.setColumnWidth(5, 5000);
      sheet.setColumnWidth(6, 4000);
      sheet.setColumnWidth(7, 4000);
      sheet.setColumnWidth(8, 4000);
      sheet.setColumnWidth(9, 4000);
      sheet.createFreezePane(0, 1);
//    sheet.setDefaultColumnWidth(20);

      CellStyle headerStyle = workbook.createCellStyle();
      XSSFFont font = workbook.createFont();
      font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
      font.setFontHeight(18);

      headerStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
      headerStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
      headerStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
      headerStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
      headerStyle.setFillForegroundColor((short) 200);
      headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
      headerStyle.setFont(font);

      CreationHelper createHelper = workbook.getCreationHelper();
      XSSFCellStyle hlinkstyle = workbook.createCellStyle();
      XSSFFont hlinkfont = workbook.createFont();
      hlinkfont.setUnderline(XSSFFont.U_SINGLE);
      hlinkfont.setColor(HSSFColor.BLUE.index);
      hlinkstyle.setFont(hlinkfont);

      int rowCount = 0;
      int columnCount = 0;

      Row row = sheet.createRow(rowCount);
      Cell cell = row.createCell(++columnCount);
      cell.setCellValue("Tên");
      cell.setCellStyle(headerStyle);

      cell = row.createCell(++columnCount);
      cell.setCellValue("SDT");
      cell.setCellStyle(headerStyle);

      cell = row.createCell(++columnCount);
      cell.setCellValue("Email");
      cell.setCellStyle(headerStyle);

      cell = row.createCell(++columnCount);
      cell.setCellValue("Price");
      cell.setCellStyle(headerStyle);

      cell = row.createCell(++columnCount);
      cell.setCellValue("No of Posts");
      cell.setCellStyle(headerStyle);

      cell = row.createCell(++columnCount);
      cell.setCellValue("Link");
      cell.setCellStyle(headerStyle);

      for (ContactPresenter contact : list) {
        row = sheet.createRow(++rowCount);

        if (this.businessService.isSale(contact.getName(), contact.getEmail())) {
          continue;
        }
        columnCount = 0;
        cell = row.createCell(columnCount);
        cell.setCellValue(rowCount);
        cell = row.createCell(++columnCount);
        cell.setCellValue(contact.getName());
        cell = row.createCell(++columnCount);
        cell.setCellValue(contact.getPhone());
        cell = row.createCell(++columnCount);
        cell.setCellValue(contact.getEmail());
        cell = row.createCell(++columnCount);
        cell.setCellValue(contact.getPrice() != null ? contact.getPrice().toString() : null);
        cell = row.createCell(++columnCount);
        cell.setCellValue(contact.getCount());
        cell = row.createCell(++columnCount);
        XSSFHyperlink link = (XSSFHyperlink)createHelper.createHyperlink(Hyperlink.LINK_URL);
        link.setAddress(contact.getPostUrl() != null ? contact.getPostUrl() : "");
        cell.setCellValue(contact.getPostUrl());
        cell.setHyperlink((XSSFHyperlink) link);
        cell.setCellStyle(hlinkstyle);
      }
    }

    XSSFSheet sheet = workbook.createSheet("Tổng kết");
    sheet.setColumnWidth(0, 5000);
    sheet.setColumnWidth(2, 3000);

    Set<String> keys = summary.keySet();
    Iterator<String> it = keys.iterator();
    int rowCount = 0;

    while (it.hasNext()) {
      int columnCount = 0;
      String key = it.next();
      int value = summary.get(key);
      Row row = sheet.createRow(rowCount++);
      Cell cell = row.createCell(columnCount++);
      cell.setCellValue(key);
      cell = row.createCell(columnCount++);
      cell.setCellValue(value);
    }
    Row row = sheet.createRow(rowCount++);
    row = sheet.createRow(rowCount++);
    Cell cell = row.createCell(0);
    cell.setCellValue("Tổng");
    cell = row.createCell(1);
    cell.setCellValue(total);


    File dir = new File ("exports");
    if (!dir.exists() || !dir.isDirectory()) {
      dir.mkdir();
    }
    SimpleDateFormat spd = new SimpleDateFormat("yyyyMMdd_hhmmss");
    String date = spd.format(new Date());
    try (FileOutputStream outputStream = new FileOutputStream(String.format("%s/%s_%s.xlsx", dir.getName(), "Phuc", date))) {
      workbook.write(outputStream);
    }
  }

  @Override
  public void exportContacts(String email, String city, Integer noOfPosts, Boolean onlyNewData) throws IOException {

    Customer customer = this.crawledDataDao.getCustomerByEmail(email);
    Set<Long> contactIdList = new HashSet<>();

    if (customer == null) {
      return;
    }

    List<LocationPresenter> locations = this.crawledDataDao.getAllLocationsByCity(city);
    XSSFWorkbook workbook = new XSSFWorkbook();

    Map<String, Integer> summary = new HashMap<>();

    int total = 0;
    for (LocationPresenter location : locations) {
      List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(location.getId(), noOfPosts);
      if (list.size() == 0) {
        continue;
      }

      XSSFSheet sheet = workbook.createSheet(location.getDistrict() + " (" + list.size() + ")");
      int sheetIndex = workbook.getSheetIndex(sheet);

      sheet.autoSizeColumn(0);
      sheet.setColumnWidth(1, 5000);
      sheet.setColumnWidth(2, 3000);
      sheet.setColumnWidth(3, 7000);
      sheet.setColumnWidth(4, 4000);
      sheet.setColumnWidth(5, 5000);
      sheet.setColumnWidth(6, 4000);
      sheet.setColumnWidth(7, 4000);
      sheet.setColumnWidth(8, 4000);
      sheet.setColumnWidth(9, 4000);
      sheet.createFreezePane(0, 1);
//    sheet.setDefaultColumnWidth(20);

      CellStyle headerStyle = workbook.createCellStyle();
      XSSFFont font = workbook.createFont();
      font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
      font.setFontHeight(18);

      headerStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
      headerStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
      headerStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
      headerStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
      headerStyle.setFillForegroundColor((short) 200);
      headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
      headerStyle.setFont(font);

      CreationHelper createHelper = workbook.getCreationHelper();
      XSSFCellStyle hlinkstyle = workbook.createCellStyle();
      XSSFFont hlinkfont = workbook.createFont();
      hlinkfont.setUnderline(XSSFFont.U_SINGLE);
      hlinkfont.setColor(HSSFColor.BLUE.index);
      hlinkstyle.setFont(hlinkfont);

      int rowCount = 0;
      int columnCount = 0;

      Row row = sheet.createRow(rowCount);
      Cell cell = row.createCell(++columnCount);
      cell.setCellValue("Tên");
      cell.setCellStyle(headerStyle);

      cell = row.createCell(++columnCount);
      cell.setCellValue("SDT");
      cell.setCellStyle(headerStyle);

      cell = row.createCell(++columnCount);
      cell.setCellValue("Email");
      cell.setCellStyle(headerStyle);

      cell = row.createCell(++columnCount);
      cell.setCellValue("Price");
      cell.setCellStyle(headerStyle);

      cell = row.createCell(++columnCount);
      cell.setCellValue("No of Posts");
      cell.setCellStyle(headerStyle);

      cell = row.createCell(++columnCount);
      cell.setCellValue("Link");
      cell.setCellStyle(headerStyle);

      for (ContactPresenter contact : list) {

        if (onlyNewData && this.crawledDataDao.isContactExported(customer.getId(), contact.getId())) {
          continue;
        }

        contactIdList.add(contact.getId());

        row = sheet.createRow(++rowCount);

        if (this.businessService.isSale(contact.getName(), contact.getEmail())) {
          continue;
        }
        columnCount = 0;
        cell = row.createCell(columnCount);
        cell.setCellValue(rowCount);
        cell = row.createCell(++columnCount);
        cell.setCellValue(contact.getName());
        cell = row.createCell(++columnCount);
        cell.setCellValue(contact.getPhone());
        cell = row.createCell(++columnCount);
        cell.setCellValue(contact.getEmail());
        cell = row.createCell(++columnCount);
        cell.setCellValue(contact.getPrice() != null ? contact.getPrice().toString() : null);
        cell = row.createCell(++columnCount);
        cell.setCellValue(contact.getCount());
        cell = row.createCell(++columnCount);
        XSSFHyperlink link = (XSSFHyperlink)createHelper.createHyperlink(Hyperlink.LINK_URL);
        link.setAddress(contact.getPostUrl() != null ? contact.getPostUrl() : "");
        cell.setCellValue(contact.getPostUrl());
        cell.setHyperlink((XSSFHyperlink) link);
        cell.setCellStyle(hlinkstyle);
      }
      if (sheet.getLastRowNum() == 0) {
        workbook.removeSheetAt(sheetIndex);
      } else {
        workbook.setSheetName(sheetIndex, location.getDistrict() + " (" + rowCount + ")");
        summary.put(location.getDistrict(), rowCount);
        total += rowCount;

      }
    }

    XSSFSheet sheet = workbook.createSheet("Tổng kết");
    sheet.setColumnWidth(0, 5000);
    sheet.setColumnWidth(2, 3000);

    Set<String> keys = summary.keySet();
    Iterator<String> it = keys.iterator();
    int rowCount = 0;

    while (it.hasNext()) {
      int columnCount = 0;
      String key = it.next();
      int value = summary.get(key);
      Row row = sheet.createRow(rowCount++);
      Cell cell = row.createCell(columnCount++);
      cell.setCellValue(key);
      cell = row.createCell(columnCount++);
      cell.setCellValue(value);
    }
    Row row = sheet.createRow(rowCount++);
    row = sheet.createRow(rowCount++);
    Cell cell = row.createCell(0);
    cell.setCellValue("Tổng");
    cell = row.createCell(1);
    cell.setCellValue(total);


    File dir = new File ("exports");
    if (!dir.exists() || !dir.isDirectory()) {
      dir.mkdir();
    }
    SimpleDateFormat spd = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String date = spd.format(new Date());
    try (FileOutputStream outputStream = new FileOutputStream(
        String.format("%s/%s_%s_%s_%s_%s_%s.xlsx",
            dir.getName(), customer.getName(), date, city.replace(" ", "_"), onlyNewData ? "New" : "ALL", noOfPosts, total))) {
      workbook.write(outputStream);
    }

    for(Long id : contactIdList) {
      this.crawledDataDao.trackExport(customer.getId(), id);
    }
  }

  @Override
  public void testEmail() throws MessagingException, InterruptedException {
    List<String> emails = this.crawledDataDao.getAllEmailsNotCheckedYet();
    List<String> temps = new ArrayList<>();
    temps.add("hoanhhao@gmail.com");
    int count = 0;
    for (String email : emails) {
//        if (count > 150) {
//          break;
//        }
        count++;
        JavaMailService.testEmail(email);
        this.crawledDataDao.updateEmailExisting(email, true);
    }
  }

  @Override
  public List<CityPresenter> getCitiesAndDistricts() {
    return this.crawledDataDao.getCitiesAndDistricts();
  }
}
