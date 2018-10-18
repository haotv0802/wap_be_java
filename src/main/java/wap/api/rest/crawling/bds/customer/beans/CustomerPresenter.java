package wap.api.rest.crawling.bds.customer.beans;

import java.util.Date;

/**
 * Created by haoho on 10/17/18 16:29.
 */
public class CustomerPresenter {
  private Long id;
  private String name;
  private String phone;
  private String email;
  private Date latestExportAt;
  private Date createdAt;
  private Date updatedAt;
  private Boolean updated = false;

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

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Boolean getUpdated() {
    return updated;
  }

  public void setUpdated(Boolean updated) {
    this.updated = updated;
  }
}
