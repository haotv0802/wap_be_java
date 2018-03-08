package wap.api.rest.crawling.bds.beans;

import java.util.List;

public class CityPresenter {
  private String city;

  private List<District> districts;

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public List<District> getDistricts() {
    return districts;
  }

  public void setDistricts(List<District> districts) {
    this.districts = districts;
  }

}
