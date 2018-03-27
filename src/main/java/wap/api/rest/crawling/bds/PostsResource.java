package wap.api.rest.crawling.bds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wap.api.rest.crawling.bds.interfaces.IPostsService;

/**
 * Date: 03/27/2018
 *
 * @author haoho
 */
@RestController("bdsPostsResource")
@RequestMapping(path = "/svc/bds")
public class PostsResource {

  private final Logger LOGGER = LogManager.getLogger(getClass());

  private final IPostsService postsService;

  @Autowired
  public PostsResource(
      @Qualifier("bdsPostsService") IPostsService postsService
  ) {
    Assert.notNull(postsService);

    this.postsService = postsService;
  }

  @GetMapping("/posts/count")
  public Integer getPostsCount() {
    return this.postsService.getPostsCount();
  }
}