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
  private Set<Item> products;

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

  public Set<Item> getProducts() {
    return products;
  }

  public void setProducts(Set<Item> products) {
    this.products = products;
  }
}
