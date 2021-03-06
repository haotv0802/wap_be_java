package wap.api.rest.crawling.bds.beans;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Date: 10/20/2017 Time: 10:56 AM
 * This Category Product is to be presented on Front-end.
 *
 * @author haho
 */
public class ItemPresenter {
  private String title;
  private String address;
  private String description;
  private String contactName;
  private String contactNumber;
  private String contactEmail;
  private Date publishDate;
  private Date endDate;
  private String url;
  private String district;
  private String city;
  private String type;
  private String propertyType;
  private BigDecimal acreage;
  private BigDecimal price;

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ItemPresenter)) {
      return false;
    }
    ItemPresenter comparedItem = (ItemPresenter) obj;
    if (comparedItem.getContactNumber().equals(this.getContactNumber())
        && comparedItem.getContactEmail().equals(this.getContactEmail())) {
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPropertyType() {
    return propertyType;
  }

  public void setPropertyType(String propertyType) {
    this.propertyType = propertyType;
  }
}