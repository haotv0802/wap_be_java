package wap.api.rest.crawling.bds.customer.beans;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by haoho on 04/03/19 16:29.
 */
public class CustomerUpdate {
  @NotNull
  private Long id;

  @NotNull
  @Size(min = 1, max = 30)
  private String name;

  @NotNull
  @Size(min = 1, max = 30)
  private String phone;

  @NotNull
  @Size(min = 1, max = 30)
  private String email;

  private Date latestExportAt;

  @NotNull
  private Date createdAt;

  private Date updatedAt;

  @NotNull
  private Boolean updated = false;

  @NotNull
  private Boolean isDeleted = false;

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

  public Boolean getDeleted() {
    return isDeleted;
  }

  public void setDeleted(Boolean deleted) {
    isDeleted = deleted;
  }
}
