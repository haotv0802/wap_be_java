package wap.api.rest.crawling.bds.beans;

import java.util.Set;

/**
 * Date: 10/19/2017 Time: 5:00 PM
 * This Category is to be stored in Database (Not for presentation on Front-end).
 *
 * @author haho
 */
public class Category {
  private String name;
  private String url;
  private Set<Item> items;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Category)) {
      return false;
    }
    Category comparedCategory = (Category) obj;
    if (comparedCategory.getName().equals(this.getName())) {
      return true;
    } else {
      return false;
    }
  }

  public Set<Item> getItems() {
    return items;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
