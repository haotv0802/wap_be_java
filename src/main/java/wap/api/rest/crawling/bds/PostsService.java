package wap.api.rest.crawling.bds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.interfaces.IPostsDao;
import wap.api.rest.crawling.bds.interfaces.IPostsService;

/**
 * Created by haoho on 3/27/18 17:15.
 */
@Service("bdsPostsService")
public class PostsService implements IPostsService {

  private final IPostsDao postsDao;

  @Autowired
  public PostsService(@Qualifier("bdsPostsDao") IPostsDao postsDao) {
    Assert.notNull(postsDao);

    this.postsDao = postsDao;
  }

  @Override
  public Integer getPostsCount() {
    return this.postsDao.getPostsCount();
  }
}
