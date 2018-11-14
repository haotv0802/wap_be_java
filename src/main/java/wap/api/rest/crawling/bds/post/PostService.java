package wap.api.rest.crawling.bds.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.post.beans.PostPresenter;

/**
 * Created by haoho on 11/9/18 11:43.
 */
@Service("bdsPostService")
public class PostService implements IPostService {

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
}
