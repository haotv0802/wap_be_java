package wap.api.rest.crawling.bds;

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
import wap.api.rest.crawling.bds.beans.Contact;
import wap.api.rest.crawling.bds.beans.Criterion;
import wap.api.rest.crawling.bds.beans.ItemPresenter;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataDao;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Date: 10/20/2017 Time: 10:19 AM
 * TODO: WRITE THE DESCRIPTION HERE
 *
 * @author haho
 */
@Repository("bdsCrawledDataService")
public class CrawledDataService implements ICrawledDataService {
  private final ICrawledDataDao crawledDataDao;

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Autowired
  public CrawledDataService(@Qualifier("bdsCrawledDataDao") ICrawledDataDao crawledDataDao) {
    Assert.notNull(crawledDataDao);

    this.crawledDataDao = crawledDataDao;
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
  public List<Contact> getContacts() {
    return this.crawledDataDao.getContacts();
  }

}
