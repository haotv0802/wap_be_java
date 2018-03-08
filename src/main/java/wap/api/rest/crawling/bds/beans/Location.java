package wap.api.rest.crawling.bds.beans;

import java.util.Map;

public class Location {
  private String city;
  private Map<Integer, String> districts;

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Map<Integer, String> getDistricts() {
    return districts;
  }

  public void setDistricts(Map<Integer, String> districts) {
    this.districts = districts;
  }
}
