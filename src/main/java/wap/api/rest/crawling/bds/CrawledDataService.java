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
    List<String> temps = new ArrayList<>();
    temps.add("hoanhhao@gmail.com");
    temps.add("nguyenminhyennhi94@gmail.com");
    for (String email : temps) {
      JavaMailService.sendAdsToCustomer(email);
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
    for (String email : emails) {
        JavaMailService.testEmail(email);
        this.crawledDataDao.updateEmailExisting(email, true);
    }
  }

}
