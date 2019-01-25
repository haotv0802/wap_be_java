package wap.api.rest.crawling.bds.post;

import org.springframework.data.domain.Pageable;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.post.beans.PostPresenter;

import java.util.Date;

/**
 * Created by haoho on 11/9/18 11:42.
 */
public interface IPostDao {
  ISlice<PostPresenter> getPostsByContactId(Pageable pageable, Long contactId);

  Integer getPostsCountByContactId(Long contactId);

  Integer getNumberOfReport(String date);
}
