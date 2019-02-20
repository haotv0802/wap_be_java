package wap.api.rest.crawling.bds.post;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.post.beans.PostPresenter;
import wap.api.rest.crawling.bds.post.beans.PostReportPresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by haoho on 11/9/18 11:43.
 */
@Service("bdsPostService")
public class PostService implements IPostService {

  private static final Logger LOGGER = LogManager.getLogger(PostService.class);

  final private IPostDao postDao;

  @Autowired
  public PostService(@Qualifier("bdsPostDao") IPostDao postDao) {
    Assert.notNull(postDao);

    this.postDao = postDao;
  }

  @Override
  public ISlice<PostPresenter> getContactsByContactId(Pageable pageable, Long contactId) {
    return postDao.getPostsByContactId(pageable, contactId);
  }

  @Override
  public Integer getPostsCountByContactId(Long contactId) {
    return this.postDao.getPostsCountByContactId(contactId);
  }

  @Override
  public PostReportPresenter getReportByDate(Date startDate, Date endDate) {

    SimpleDateFormat spdf = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat spdfQuery = new SimpleDateFormat("yyyy-MM-dd");
    List<String> periods = new ArrayList<>();
    List<Integer> figures = new ArrayList<>();

    Date date = startDate;
    Date end = endDate;
    end.setDate(endDate.getDate() + 1);


    for (; date.before(end); date.setDate(date.getDate() + 1)) {
      this.LOGGER.info(">>>>>>>>>>> " + date.toString());
      periods.add(spdf.format(date));

      int numberOfPosts = postDao.getNumberOfPosts(spdfQuery.format(date));
      figures.add(numberOfPosts);
    }

    PostReportPresenter postReportPresenter = new PostReportPresenter();
    postReportPresenter.setFigures(figures);
    postReportPresenter.setPeriods(periods);

    return postReportPresenter;
  }

  @Override
  public PostReportPresenter getReportByMonth(Integer startMonth, Integer startYear, Integer endMonth, Integer endYear) {

    List<String> periods = new ArrayList<>();
    List<Integer> figures = new ArrayList<>();

    for (int i = startYear; i <= endYear; i++) {
      int end = 12;
      int start;
      if (i > startYear) {
        start = 1;
      } else {
        start = startMonth;
      }

      if (i == endYear) {
        end = endMonth;
      }

      for (int j = start; j <= end; j++) {
        periods.add(String.format("%02d", j) + "/" + i);
        int numOfPosts = postDao.getNumberOfPostsByMonth(j, i);
        figures.add(numOfPosts);
      }
    }

    PostReportPresenter postReportPresenter = new PostReportPresenter();
    postReportPresenter.setFigures(figures);
    postReportPresenter.setPeriods(periods);

    return postReportPresenter;
  }
}
