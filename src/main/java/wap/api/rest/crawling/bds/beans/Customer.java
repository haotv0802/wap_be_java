package wap.api.rest.crawling.bds.beans;

import java.util.Date;

public class Customer {
  private Long id;
  private String name;
  private String phone;
  private String email;
  private Date latestExportAt;
  private Date updatedAt;

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

  public Date getLatestExportAt() {
    return latestExportAt;
  }

  public void setLatestExportAt(Date latestExportAt) {
    this.latestExportAt = latestExportAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
}
