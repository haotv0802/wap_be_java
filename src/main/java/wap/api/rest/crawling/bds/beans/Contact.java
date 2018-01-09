package wap.api.rest.crawling.bds.beans;

import java.util.Date;

public class Contact {
  private Long id;
  private String name;
  private String phone;
  private String email;
  private String type;
  private String url;
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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
