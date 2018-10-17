package wap.api.rest.crawling.bds.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wap.api.rest.auth.ISlice;
import wap.api.rest.auth.Slice;
import wap.api.rest.crawling.bds.customer.beans.CustomerPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoho on 10/17/18 16:48.
 */
@Repository("bdsCustomerDao")
public class CustomerDao implements ICustomerDao {

  private static final Logger LOGGER = LogManager.getLogger(CustomerDao.class);

  private final NamedParameterJdbcTemplate namedTemplate;

  @Autowired
  public CustomerDao(NamedParameterJdbcTemplate namedTemplate) {
    this.namedTemplate = namedTemplate;
  }


  @Override
  public ISlice<CustomerPresenter> getCusomters(Pageable pageable, String name, String phone, String email) {

    List list = new ArrayList();
    CustomerPresenter presenter = new CustomerPresenter();
    presenter.setId(new Long(1));
    presenter.setName("name 1");
    presenter.setEmail("email 1");
    presenter.setPhone("phone 1");
    list.add(presenter);

    presenter = new CustomerPresenter();
    presenter.setId(new Long(2));
    presenter.setName("name 2");
    presenter.setEmail("email 2");
    presenter.setPhone("phone 2");
    list.add(presenter);

    presenter = new CustomerPresenter();
    presenter.setId(new Long(3));
    presenter.setName("name 3");
    presenter.setEmail("email 3");
    presenter.setPhone("phone 3");
    list.add(presenter);

    ISlice slice = new Slice(list, pageable, true, 10);
    return slice;
  }
}
