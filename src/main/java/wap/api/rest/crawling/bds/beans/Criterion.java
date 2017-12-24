package wap.api.rest.crawling.bds.beans;

public class Criterion {
  private String personName;
  private String district;
  private String city;
  private String acreageLessThan;
  private String acreageLargerThan = "0";
  private String priceLessThan;
  private String priceLargerThan = "0";

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

  public String getAcreageLessThan() {
    return acreageLessThan;
  }

  public void setAcreageLessThan(String acreageLessThan) {
    this.acreageLessThan = acreageLessThan;
  }

  public String getAcreageLargerThan() {
    return acreageLargerThan;
  }

  public void setAcreageLargerThan(String acreageLargerThan) {
    this.acreageLargerThan = acreageLargerThan;
  }

  public String getPriceLessThan() {
    return priceLessThan;
  }

  public void setPriceLessThan(String priceLessThan) {
    this.priceLessThan = priceLessThan;
  }

  public String getPriceLargerThan() {
    return priceLargerThan;
  }

  public void setPriceLargerThan(String priceLargerThan) {
    this.priceLargerThan = priceLargerThan;
  }

  public String getPersonName() {
    return personName;
  }

  public void setPersonName(String personName) {
    this.personName = personName;
  }
}
