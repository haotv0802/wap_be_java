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

    List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(14); // Tan Binh
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(7));
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(9));
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(16));
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(23));
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(17));
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(10));
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(4));
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(22));

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

    String title = "Ưu đãi cực lớn Block cuối cùng và đẹp nhất khu EMERALD dự án Celadon city !!!";

    String emailBody = "Xin chào Anh/Chị, <br/> "
        + "Celadon city thông báo đến quý khách hàng chương trình bán hàng block E ngày 18/3/2018; là block cuối cùng của Emerald. <br><br>"
        + "****Nhận giữ chổ ngày 10/3/2018, 50tr/ giữ chổ.<br>"
        + "** Chiếc khấu : 5% trên giá bán.<br>"
        + "** Kết nối cộng đồng: ck 1%<br>"
        + "** Tri ân khách hàng: ck 1%<br>"
        + "** Voucher mừng xuân: 25tr<br>"
        + "** Gói chăm sóc sức khoẻ Hoàn Mỹ cho gia đình trong 1 năm.<br>"
        + "** Bốc thăm iphone X, ipad, note 8<br>"
        + "Quý khách hàng đăng ký tham gia chương trình báo e book ngay cho Pkd nhé vì số lượng có hạn ạ!<br>"
        + "<b>Hotline: 0906 99 61 69 (viber/zalo)</b><br>"
        + "   Email: yennhi.gamudaland@gmail.com <br>"
        + "*** Phương thức thanh toán :<br>"
        + "- Ký hợp đồng 15%<br>"
        + "- 4 tháng sau 15%<br>"
        + "- Cất nóc 15%<br>"
        + "- Nhận nhà 30%<br>"
        + "- Sau 3 tháng nhận nhà 20%<br>"
        + "- Cấp sổ 5%<br>"
        + "Trân trọng!<br>"

        + "<img src=\"https://lh3.googleusercontent.com/BjIF5JRmU87nS5mHMbmac_JoZg-EP0e1JveEe4ARc00Un4LgFEpQ3RXwRu3lq6tD_nIvJK-336xNVVHuTeHSspuVb1g8TKFmjfVNnqaaiQ5W8jS64Hjrm9sX9Nvhs6jt5xrR8ALEcu-KeLqWuektJIshnuaQ1KuzLumb9BkaNs-CCgxSqSCMiXxQPfaTIOfTHdftGOhShDLzVrC2THn7OE-fPMxnJAnSQlW6v_VbdZjqyDkRC8iFD-k9WMSsOXTRm823D3__z_C_p9hK7_95R0MAdVrClIKm0FXdZtyvhOtXM4ZBpbViJM6BNyVr1quHjIi6qKAVLob5R7u0EeErmACvyJ8W1Htd4z8p1y7GTvEc5BYAlV91iqb9E39ys-4MQHXlWB4-diICOtFg-RpFA7ZQbBFsfECNSvgQXDB5Dz3fZmrYzWLguF72J-YRk3fY-YEcSxLabcpCPy8sEK3AT0PJKvSKIQ-nMOn-DeLx5JVfDfTcM_CV-0PRhTumjgnDrG5Uvm_kDpR-FaCUcQ0QG_VAHFGmjToXoq-m1J4He5d2fQd-tK58xfXGymc6ByNrplSnzuUI2LDo_1vl2x2PG0WDKT_9T38=w960-h668-no\">"
        + "<img src=\"https://lh3.googleusercontent.com/gj0htd3Rpub_H9OMCovUNmW16OUSVX2MirFjSjH_5sYOg0lN5ht7LJntlnxem34OZptY8_owVm8SL1KmT6sWKiNyfEnigiLqZ9b88DfNhwpkI42J6Qb0Rro-cFycrJFJU-YM_roQbhAtv5ebY1jAugELW_006Jd8LagUD6LXyxf4mJAdBmVNVB5yI4ecrr5GIJpbFF7U_DhOFEEyqCUyHMhST6yH0WL9ugvRUoUSew-xjNU3bjjzqKwGS80CLCptAxz7RNQpltWPdBnAUO5SnPORKJ1BfNBMmj3ypVYrvj9QvrAwcDMJ-IiyA1Bw-KeI7CJaguYV3YCDZafZWR_-X5_ofWKo4FcXoCUkB_BMNxDbWvdavnfRBMMr8nS-5S7AhCyIj5uu_fc5YIpxX2PpmlA5KxwF3MiIvggzbIAuEn3NIsUABl3ODjtZdXJM3pD4bi-s5pLxdtlYnTIjKMreX2Tx5gJ1uUDthYxbuM5ndqgJaKItWkGv5Y-GS749I816-vh6lNk5-lmj4PEIzlKMQIVRYDWPzK67b3cSXAfoJSl8WSkl_gLG8kxqIa8N3Y4XyYi4tvDc7z5GTbpEfWnOt6aS7sqPG20=w894-h696-no\">"
        + "<img src=\"https://lh3.googleusercontent.com/oYb03oG2aIeDF0jdIiR30jgfhgePxsi9XNHHn0vXaMbjYCsNixlmBNcaclJ61ZB8tH1c4xwtJYzJK8HOhGgM4twsVBONRbGJpPRgRr5xFyq8S-ZathzfB9rc9H7d7aK_7E3KFHyULlE2wgiXLRtVNmKpevtZdXcNziXetSd0H-LJcxQdfZs5EDm-QmrcIppMQxNaFoJ6tyPt_IiwP1HVfQZV6AEmMuzXKoC6wZHphqBI1CYdk698p8r0XNkSe8pBm5uLs6XInAQ-XDXaMhs6kQNlHLe6BJWjWoDKzCFr_GCenNd5tXphFD-1wYZpSCxMJ556wn55y9mAb4JuRTWA_m7Azx6EqFAiCkD-3ezHzbd2ojGHu7x7LVIrihmiC4tfuYzGKMVNwsNlSyqX3nkdDw3fWppkrIx5yecvssi4zqF9AqdTugooNKMVGOAJ6qDdhbpcF1a9dfdGLoIMna8P-3s-sLSH_UHpsUllsRPV2j-kOdCazZ6fSy0yxxvVS62q8iMVJ-0cKkafN9-PHfHAoKAjl6IO4iTVorF6LqVd4gws4fn6YLABwldLK3W_NMkIRtSN5mPGGRFTj4X9xSY1ys98piXEgW0=w960-h719-no\">"
        + "<img src=\"https://lh3.googleusercontent.com/1mqEC5AGyKuuaj-s9b_dBF5svEDN_QwT60faGQxEoXVGKq9IKqComRixPnB4zklQn4FuGc2uU3NHBoWCRTDrIRH9AXl97fgYtjLN7YgNmUdFCKRFT_EIuttlh4h0yoDDjWQ0RN-7NOWT5uMktOmXIt8jxaevmEcNvtmI5XgfyN1_s3OJalyO2-bNgYu8vOJYec-Hxlzc9iwF9BiPolzhjmUNPgtY_uVucNPF3FVIKGKf77Pywd1sBOATBqpk9gQZek-cm-0lh5Anu_Sv6mKnkCxZsXvEIGZcEHI7V4IRyXWaY-eoQo0_tLQZ_ViurKgJ5AED4BOV1c57wSke4c18IRAHskGpR6rwSL7rOoAuwOzFhnDksTZ0mlgZZkVS1tqoouR3W27m-sTOCyqFtZe12PyJUlkV6qTAnci5iXtAsvP_vOwfoK6oBVkuvZKiUNRO7b-T3essFQzOl3hfakFsbd_8fiqOuEYiehzRGkBVcZ1LDcaNJEY5PxeMqArSQ_IwiJm0_Ysc31PKcHR6kS2SYoKH4bClqCpt0aKsg6tAp5Xe-Zz8ai5wkzseKCvb1kjuTTy_ZNSaeCsi8kPtctC3uMCjt19AC-A=w800-h533-no\">"
        ;
    String from;
    String fromPass;
    if (iterator.hasNext()) {
      from = iterator.next();
      fromPass = emailAccounts.get(from);

      for (ContactPresenter contactPresenter : list) {
        System.out.println("..... " + from + "  >>>>> " + contactPresenter.getEmail());
        if (!this.crawledDataDao.checkEmailSentOrNotWithTitle(title, contactPresenter.getEmail())) {
          try {
            JavaMailService.sendAdsToCustomer(from, fromPass, contactPresenter.getEmail(), title, emailBody);
            this.crawledDataDao.trackEmailSent(from, contactPresenter.getEmail(), title, emailBody);
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
}
