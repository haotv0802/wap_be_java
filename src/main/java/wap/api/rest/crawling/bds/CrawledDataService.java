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

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Autowired
  public CrawledDataService(
      @Qualifier("bdsCrawledDataDao") ICrawledDataDao crawledDataDao,
      @Qualifier("bdsBusinessService") BusinessService businessService
  ) {
    Assert.notNull(crawledDataDao);
    Assert.notNull(businessService);

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
    List<ContactPresenter> list = this.crawledDataDao.getOwnerContactsByLocation(3);
//
//    List<String> temps = new ArrayList<>();
//    temps.add("hoanhhao@gmail.com");
//    temps.add("nguyenminhyennhi94@gmail.com");

    String from = "yennhi.gamuda@gmail.com";

    String fromPass = "hpzitbyzeslypetm";

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
        XSSFHyperlink link = (XSSFHyperlink)createHelper.createHyperlink(Hyperlink.LINK_URL);
        link.setAddress(contact.getPostUrl());
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
        if (count > 150) {
          break;
        }
        count++;
        JavaMailService.testEmail(email);
        this.crawledDataDao.updateEmailExisting(email, true);
    }
  }
}
