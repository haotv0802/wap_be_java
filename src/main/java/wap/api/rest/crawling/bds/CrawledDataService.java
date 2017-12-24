package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.beans.Criterion;
import wap.api.rest.crawling.bds.beans.ItemPresenter;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataDao;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataService;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

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
    XSSFSheet sheet = workbook.createSheet("Java Books");

    Object[][] bookData = {
        {"Head First Java", "Kathy Serria", 79},
        {"Effective Java", "Joshua Bloch", 36},
        {"Clean Code", "Robert martin", 42},
        {"Thinking in Java", "Bruce Eckel", 35},
    };

    int rowCount = 0;

    for (Object[] aBook : bookData) {
      Row row = sheet.createRow(++rowCount);

      int columnCount = 0;

      for (Object field : aBook) {
        Cell cell = row.createCell(++columnCount);
        if (field instanceof String) {
          cell.setCellValue((String) field);
        } else if (field instanceof Integer) {
          cell.setCellValue((Integer) field);
        }
      }

    }


    try (FileOutputStream outputStream = new FileOutputStream("JavaBooks.xlsx")) {
      workbook.write(outputStream);
    }
    return null;
  }

}
