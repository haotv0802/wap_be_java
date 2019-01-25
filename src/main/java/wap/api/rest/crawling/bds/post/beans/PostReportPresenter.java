package wap.api.rest.crawling.bds.post.beans;

import java.util.List;

/**
 * Created by haoho on 1/25/19 11:00.
 */
public class PostReportPresenter {
  private List<String> periods;
  private List<Integer> figures;

  public List<String> getPeriods() {
    return periods;
  }

  public void setPeriods(List<String> periods) {
    this.periods = periods;
  }

  public List<Integer> getFigures() {
    return figures;
  }

  public void setFigures(List<Integer> figures) {
    this.figures = figures;
  }
}
