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
    return null;
  }
}
