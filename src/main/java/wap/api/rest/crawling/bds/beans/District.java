package wap.api.rest.crawling.bds.beans;

public class District {
  private Integer id;
  private String district;

  public District(Integer id, String district) {
    this.id = id;
    this.district = district;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }
}