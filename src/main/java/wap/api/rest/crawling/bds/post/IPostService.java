package wap.api.rest.crawling.bds.post;

import org.springframework.data.domain.Pageable;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.post.beans.PostPresenter;
import wap.api.rest.crawling.bds.post.beans.PostReportPresenter;

import java.util.Date;

/**
 * Created by haoho on 11/9/18 11:41.
 */
public interface IPostService {
  ISlice<PostPresenter> getContactsByContactId(Pageable pageable, Long contactId);

  Integer getPostsCountByContactId(Long contactId);

  PostReportPresenter getReportByDate(Date startDate, Date endDate);

  PostReportPresenter getReportByMonth(Integer startMonth, Integer startYear, Integer endMonth, Integer endYear);
}
