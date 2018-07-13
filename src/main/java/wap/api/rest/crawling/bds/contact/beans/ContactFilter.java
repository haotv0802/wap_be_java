package wap.api.rest.crawling.bds.contact.beans;

/**
 * Created by haoho on 7/13/18 09:30.
 */
public class ContactFilter {
  private String nameFilter;
  private String phoneFilter;
  private String emailFilter;
  private String typeFilter;
  private String manualCheckFilter;
  private String emailExistingFilter;

  public String getNameFilter() {
    return nameFilter;
  }

  public void setNameFilter(String nameFilter) {
    this.nameFilter = nameFilter;
  }

  public String getPhoneFilter() {
    return phoneFilter;
  }

  public void setPhoneFilter(String phoneFilter) {
    this.phoneFilter = phoneFilter;
  }

  public String getEmailFilter() {
    return emailFilter;
  }

  public void setEmailFilter(String emailFilter) {
    this.emailFilter = emailFilter;
  }

  public String getTypeFilter() {
    return typeFilter;
  }

  public void setTypeFilter(String typeFilter) {
    this.typeFilter = typeFilter;
  }

  public String getManualCheckFilter() {
    return manualCheckFilter;
  }

  public void setManualCheckFilter(String manualCheckFilter) {
    this.manualCheckFilter = manualCheckFilter;
  }

  public String getEmailExistingFilter() {
    return emailExistingFilter;
  }

  public void setEmailExistingFilter(String emailExistingFilter) {
    this.emailExistingFilter = emailExistingFilter;
  }
}
