package wap.api.rest.crawling.bds.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * Date: 10/19/2017 Time: 5:00 PM
 * This Category is to be stored in Database (Not for presentation on Front-end).
 *
 * @author haho
 */
public class Category {
  private Long id;
  private String name;
  private String url;
  private Integer itemsCount = 0;
  private Integer itemsCrawled = 0;
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
    if (comparedCategory.getUrl().equals(this.getUrl())) {
      return true;
    } else {
      return false;
    }
  }

  public Set<Item> getItems() {
    return items;
  }

  public void addItems(Set<Item> items) {
    if (null == this.items) {
      this.items = new HashSet<>();
    }
    this.items.addAll(items);
  }
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getItemsCount() {
    return itemsCount;
  }

  public void setItemsCount(Integer itemsCount) {
    this.itemsCount = itemsCount;
  }

  public Integer getItemsCrawled() {
    return itemsCrawled;
  }

  public void setItemsCrawled(Integer itemsCrawled) {
    this.itemsCrawled = itemsCrawled;
  }
}