package wap.api.rest.crawling.bds.beans;

import java.math.BigDecimal;
import java.util.Date;

public class ContactPresenter {
  private Long id;
  private String name;
  private String phone;
  private String email;
  private String type;
  private BigDecimal price;
  private int count;
  private String postUrl;
  private Date latestItemPostedOn;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Date getLatestItemPostedOn() {
    return latestItemPostedOn;
  }

  public void setLatestItemPostedOn(Date latestItemPostedOn) {
    this.latestItemPostedOn = latestItemPostedOn;
  }

  public String getPostUrl() {
    return postUrl;
  }

  public void setPostUrl(String postUrl) {
    this.postUrl = postUrl;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
