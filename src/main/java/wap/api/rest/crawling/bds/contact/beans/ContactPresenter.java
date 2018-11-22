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
  private Integer postsCount;
  private String manualCheck;
  private String emailExisting;
  private Date latestItemPostedAt;
  private Date createdAt;
  private Date updatedAt;
  private String description;
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

  public String getEmailExisting() {
    return emailExisting;
  }

  public void setEmailExisting(String emailExisting) {
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getUpdated() {
    return updated;
  }

  public void setUpdated(Boolean updated) {
    this.updated = updated;
  }

  public Integer getPostsCount() {
    return postsCount;
  }

  public void setPostsCount(Integer postsCount) {
    this.postsCount = postsCount;
  }
}
