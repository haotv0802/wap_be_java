package wap.api.rest.crawling.bds.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wap.api.rest.auth.ISlice;
import wap.api.rest.crawling.bds.post.beans.PostPresenter;
import wap.api.rest.crawling.bds.post.beans.PostReportPresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by haoho on 11/9/18 11:41.
 */
@RestController("bdsPostResource")
@RequestMapping(path = "/svc/bds/post")
public class PostResource {
  final private IPostService postService;

  @Autowired
  public PostResource(@Qualifier("bdsPostService") IPostService postService) {
    Assert.notNull(postService);

    this.postService = postService;
  }

  @GetMapping("/list")
  public ISlice<PostPresenter> getPostsByContactId(
    Pageable pageable,
    @RequestParam(value = "contactId") Long contactId
  ) {
    return this.postService.getContactsByContactId(pageable, contactId);
  }

  @GetMapping("/count/contact")
  public Integer getPostsCountByContactId(
    @RequestParam(value = "id") Long contactId
  ) {
    return this.postService.getPostsCountByContactId(contactId);
  }

  @GetMapping("/report")
  public PostReportPresenter getReport(
    @RequestParam(value = "startDate") String startDate,
    @RequestParam(value = "endDate") String endDate
  ) throws ParseException {
    SimpleDateFormat spdf = new SimpleDateFormat("dd/MM/yyyy");
    Date start = spdf.parse(startDate);
    Date end = spdf.parse(endDate);

    return this.postService.getReport(start, end);
  }
}