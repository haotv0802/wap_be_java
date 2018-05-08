package wap.api.rest.crawling.bds.contact.beans;

import java.util.Date;

/**
 * Created by haoho on 5/8/18 09:39.
 */
public class ContactPresenter {
  private Long id;
  private String name;
  private String phone;
  private String email;
  private String type;
  private String manualCheck;
  private Boolean emailExisting;
  private Date latestItemPostedAt;
  private Date createdAt;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getManualCheck() {
    return manualCheck;
  }

  public void setManualCheck(String manualCheck) {
    this.manualCheck = manualCheck;
  }

  public Boolean getEmailExisting() {
    return emailExisting;
  }

  public void setEmailExisting(Boolean emailExisting) {
    this.emailExisting = emailExisting;
  }

  public Date getLatestItemPostedAt() {
    return latestItemPostedAt;
  }

  public void setLatestItemPostedAt(Date latestItemPostedAt) {
    this.latestItemPostedAt = latestItemPostedAt;
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
}
