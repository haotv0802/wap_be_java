package wap.api.rest.auth;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

/**
 * Created by haoho on 5/9/18 17:16.
 */
public class Slice<T> extends SliceImpl<T> implements ISlice<T> {

  private final int numberOfPages;
  private final int total;

  public Slice(List<T> content, Pageable pageable, boolean hasNext, int total) {
    super(content, pageable, hasNext);
    this.total = total;
    this.numberOfPages = total / pageable.getPageSize() + (total % pageable.getPageSize() == 0 ? 0 : 1);
  }

  public int getTotal() {
    return total;
  }

  public int getNumberOfPages() {
    return numberOfPages;
  }
}
