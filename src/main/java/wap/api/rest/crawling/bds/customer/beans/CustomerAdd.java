package wap.api.rest.crawling.bds.customer.beans;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by haoho on 10/26/18 11:22.
 */
public class CustomerAdd {

  @NotNull
  @Size(min = 1, max = 20)
  private String name;

  @NotNull
  @Size(min = 1, max = 20)
  private String phone;

  @NotNull
  @Size(min = 1, max = 30)
  private String email;


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
}
