package wap.api.rest.crawling.bds;

import com.sun.mail.smtp.SMTPSendFailedException;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wap.api.rest.crawling.bds.beans.*;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataDao;
import wap.api.rest.crawling.bds.interfaces.ICrawledDataService;
import wap.api.rest.crawling.bds.interfaces.IEmailAccountService;
import wap.api.rest.crawling.bds.interfaces.IEmailsService;
import wap.api.rest.crawling.mailing.JavaMailService;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Date: 10/20/2017 Time: 10:19 AM
 *
 * @author haho
 */
@Repository("bdsEmailsService")
public class EmailsService implements IEmailsService {

  private final ICrawledDataDao crawledDataDao;

  private final BusinessService businessService;

  private final IEmailAccountService emailAccountService;

  private final Logger LOGGER = LogManager.getLogger(getClass());

  @Autowired
  public EmailsService(
      @Qualifier("bdsCrawledDataDao") ICrawledDataDao crawledDataDao,
      @Qualifier("bdsBusinessService") BusinessService businessService,
      @Qualifier("bdsEmailAccountService") IEmailAccountService emailAccountService) {

    Assert.notNull(crawledDataDao);
    Assert.notNull(businessService);
    Assert.notNull(emailAccountService);

    this.emailAccountService = emailAccountService;
    this.crawledDataDao = crawledDataDao;
    this.businessService = businessService;
  }

  @Override
  public void sendAdsEmeraldToCustomers() throws MessagingException {

    List<ContactPresenter> list = new ArrayList<>();
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(14)); // Tan Binh
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(7)); // Tan Phu
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(9)); // Thu Duc
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(16)); // Quan 7
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(23)); // Quan 2
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(17)); // Quan 1
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(10)); // Binh Tan
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(4)); // Phu Nhuan
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(22)); // Quan 3
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(5)); // Quan 10
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(3)); // Quan Go Vap
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(6)); // Quan 4
    list.addAll(this.crawledDataDao.getOwnerContactsByLocation(1)); // Quan 12

//    List<ContactPresenter> list = new ArrayList<>();
//    ContactPresenter cp = new ContactPresenter();
//    cp.setEmail("hoanhhao@gmail.com");
//    list.add(cp);
//    cp = new ContactPresenter();
//    cp.setEmail("nguyenminhyennhi94@gmail.com");
//    list.add(cp);

//    String from = "yennhi.gamuda@gmail.com";
//    String fromPass = "hpzitbyzeslypetm";

//    String from = "quangcaobds180300@gmail.com";
//    String fromPass = "ovdnnklpkxwxeocr";

//    String from = "quangcaobds180301@gmail.com";
//    String fromPass = "xrztwxsjasjzjayg";

    LOGGER.info(">>>>>>>>>> Number of customer in HCM: " + list.size());

    Map<String, String> emailAccounts = emailAccountService.getEmailsInfo();


    Set<String> keys = emailAccounts.keySet();
    Iterator<String> iterator = keys.iterator();
    String title = "SỐNG KHỎE - SỐNG VUI tại Celadon city";

    StringBuilder email = new StringBuilder();
    email.append("Xin chào Anh/Chị, <br/> ");
    email.append("Cuộc sống tận hưởng và vô cùng \"mát mẻ\" chưa bao giờ dễ dàng đến thế vì gia đình bạn có thể thoải mái bơi lội tại 3 hồ bơi hiện đại chuẩn Olympic ngay tại thành phố xanh Celadon City. <br><br>");
    email.append("\uD83C\uDF0A Hồ bơi chuẩn Olympic: được thiết kế dành riêng cho những tay bơi kỳ cựu. <br>");
    email.append("\uD83C\uDF79 Hồ bơi nghỉ dưỡng: khung cảnh sang trọng, được bày trí các ghế dài, dù xinh xắn, phù hợp cho nhu cầu tận hưởng chuẩn đẳng cấp.<br>");
    email.append("\uD83D\uDC6A Hồ bơi trẻ thơ: nơi con bạn thoải mái đùa nghịch với làn nước trong xanh.<br>");
    email.append("Với tiện ích tuyệt vời này, mọi nhu cầu thư giãn, rèn luyện sức khoẻ của từng thành viên gia đình bạn sẽ được đáp ứng trọn vẹn. Bạn đã sẵn sàng cùng Celadon City vun đầy từng khoảnh khắc vui sống xanh mát cho gia đình của mình chưa? <br>");
    email.append("<br>Celadon City - “Đô thị xanh dẫn đầu xu thế” <br><br>");
    email.append("\uD83C\uDF10 Fb: https://www.facebook.com/dothixanhceladon/ <br>");
    email.append("\uD83D\uDCDE Đăng ký tham quan nhà mẫu: <b>0906 99 61 69</b><br>");
    email.append("   Email: yennhi.gamudaland@gmail.com <br>");
    email.append("\uD83D\uDCCD Khu Nhà Mẫu Celadon City - Số 88, Đường N1, P. Sơn Kỳ, Q. Tân Phú, TP. HCM <br>");
    email.append("Trân trọng!<br>");
    email.append("<img width='100%' src=\"https://i.imgur.com/ZhrtHgR.jpg\"><br>");
    email.append("<img width='100%'  src=\"https://i.imgur.com/kjurFRV.jpg\"><br>");
    email.append("<img width='100%'  src=\"https://i.imgur.com/rWV90UH.jpg\"><br>");
    email.append("<img width='100%'  src=\"https://i.imgur.com/3Es4Kdv.png\"><br>");

    String from;
    String fromPass;
    if (iterator.hasNext()) {
      from = iterator.next();
      fromPass = emailAccounts.get(from);

      for (ContactPresenter contactPresenter : list) {
        System.out.println("..... " + from + "  >>>>> " + contactPresenter.getEmail());
        if (!this.crawledDataDao.checkEmailSentOrNotWithTitle(title, contactPresenter.getEmail())) {
          try {
            JavaMailService.sendAdsToCustomer(from, fromPass, contactPresenter.getEmail(), title, email.toString());
            this.crawledDataDao.trackEmailSent(from, contactPresenter.getEmail(), title, EmojiParser.parseToAliases(email.toString()));
            this.crawledDataDao.updateEmailExisting(contactPresenter.getEmail(), true);
          } catch (SMTPSendFailedException ex) {
            ex.printStackTrace();
            if (iterator.hasNext()) {
              from = iterator.next();
              fromPass = emailAccounts.get(from);
            } else {
              break;
            }
          }
        }
      }

    }
  }
}
