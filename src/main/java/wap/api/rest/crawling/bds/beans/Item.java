package wap.api.rest.crawling.bds.beans;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Date: 10/19/2017 Time: 4:56 PM
 * This Category Product is to be stored in Database (Not for presentation on front-end).
 * @author haho
 */
public class Item {
  private Long id;
  private String title;
  private String address;
//  private String description;
  private String contactName;
  private String contactNumber;
  private String contactEmail;
  private Date publishDate;
  private Date endDate;
  private String district;
  private String city;
  private BigDecimal acreage;
  private BigDecimal price;
  private String url;
  private Long categoryId;
  private Date crawlingStart;
  private Date crawlingEnd;
  private Long crawlingTime;

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Item)) {
      return false;
    }
    Item comparedItem = (Item) obj;
    if (comparedItem.getUrl().equals(this.getUrl())) {
      return true;
    } else {
      return false;
    }
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

//  public String getDescription() {
//    return description;
//  }
//
//  public void setDescription(String description) {
//    this.description = description;
//  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public Date getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Date publishDate) {
    this.publishDate = publishDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public Date getCrawlingStart() {
    return crawlingStart;
  }

  public void setCrawlingStart(Date crawlingStart) {
    this.crawlingStart = crawlingStart;
  }

  public Date getCrawlingEnd() {
    return crawlingEnd;
  }

  public void setCrawlingEnd(Date crawlingEnd) {
    this.crawlingEnd = crawlingEnd;
  }

  public Long getCrawlingTime() {
    return crawlingTime;
  }

  public void setCrawlingTime(Long crawlingTime) {
    this.crawlingTime = crawlingTime;
  }

  public BigDecimal getAcreage() {
    return acreage;
  }

  public void setAcreage(BigDecimal acreage) {
    this.acreage = acreage;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}