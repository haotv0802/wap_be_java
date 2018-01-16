package wap.api.rest.crawling.mailing;

import wap.api.rest.crawling.bds.beans.Contact;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class JavaMailService {
  static Properties mailServerProperties;
  static Session getMailSession;
  static MimeMessage generateMailMessage;
  static int count = 1;

  public static void main(String args[]) throws AddressException, MessagingException {
//    generateAndSendEmail();
    sendMailOfAds("hoanhhao@gmail.com");
    System.out.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
  }

  public static void sendAdsToContacts(Contact contact) throws AddressException, MessagingException {

    // Step1
    System.out.println("\n 1st ===> setup Mail Server Properties..");
    mailServerProperties = System.getProperties();
    mailServerProperties.put("mail.smtp.port", "587");
    mailServerProperties.put("mail.smtp.auth", "true");
    mailServerProperties.put("mail.smtp.starttls.enable", "true");
    System.out.println("Mail Server Properties have been setup successfully..");

    // Step2
    System.out.println("\n\n 2nd ===> get Mail Session..");
    getMailSession = Session.getDefaultInstance(mailServerProperties, null);
    generateMailMessage = new MimeMessage(getMailSession);
    generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(contact.getEmail()));
    generateMailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress("nhinguyen130905@gmail.com"));
//    generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@crunchify.com"));

    generateMailMessage.setSubject("Lời mời cung cấp dữ liệu (data) khách hàng: " + (contact.getPhone() == null ? contact.getEmail() : contact.getPhone()));
    String emailBody = "Xin chào Anh/Chị, <br/> "
        + "Em bên cung cấp dữ liệu khách hàng lấy được từ các website bất động sản, mua bán, v.v.v "
        + "và được <b>cập nhật thường xuyên</b>, không phải dữ liệu cũ lấy từ các bạn ngân hàng hoặc từ nguồn khác. <br><br>"
        + "Đơn giản là bên em sử dụng hệ thống để lấy dữ liệu khách hàng, thay vì quý anh/chị lấy dữ bằng thủ công. <br><br>"
        + "Anh/Chị có nhu cầu chỉ cần phản hồi thư này để lại thông tin, "
        + "bên em sẽ giúp lấy dữ liệu khách hàng cho anh/chị phát triển kế hoạch sale của mình ạ."

        + "<br><br><i> Xin lỗi nếu email này làm phiền quý anh chị<i> <br>"
        + "<br><i> Thông tin quý anh/chị em lấy từ đây: " + contact.getUrl() + "<i> <br>"
        + "<br><br> Trân trọng, <br>";
    generateMailMessage.setContent(emailBody, "text/html; charset=utf-8");
    System.out.println("Mail Session has been created successfully..");

    // Step3
    System.out.println("\n\n 3rd ===> Get Session and Send mail");
    Transport transport = getMailSession.getTransport("smtp");

    // Enter your correct gmail UserID and Password
    // if you have 2FA enabled then provide App Specific Password
    transport.connect("smtp.gmail.com", "vanthuc82@gmail.com", "ljkcsnbudkviblaz");
    transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
    transport.close();
  }

  public static void testEmail(String email) throws MessagingException, InterruptedException {
      // Step1
      System.out.println("\n 1st ===> setup Mail Server Properties..");
      mailServerProperties = System.getProperties();
      mailServerProperties.put("mail.smtp.port", "587");
      mailServerProperties.put("mail.smtp.auth", "true");
      mailServerProperties.put("mail.smtp.starttls.enable", "true");
      System.out.println("Mail Server Properties have been setup successfully..");

      // Step2
      System.out.println("\n\n 2nd ===> get Mail Session..");
      getMailSession = Session.getDefaultInstance(mailServerProperties, null);
      generateMailMessage = new MimeMessage(getMailSession);
      generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
//      generateMailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress("nhinguyen130905@gmail.com"));
//    generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@crunchify.com"));

      generateMailMessage.setSubject("testing");
      String emailBody = "testing";
      generateMailMessage.setContent(emailBody, "text/html; charset=utf-8");
      System.out.println("Mail Session has been created successfully..");

      // Step3
      System.out.println("\n\n 3rd ===> Get Session and Send mail");
      Transport transport = getMailSession.getTransport("smtp");

      // Enter your correct gmail UserID and Password
      // if you have 2FA enabled then provide App Specific Password
//      if (count >= 0 && count <= 100) {
//        transport.connect("smtp.gmail.com", "salomon3000@gmail.com", "vswauhzfgslzowmv");
//      } else  if (count > 100 && count <= 200) {
//        transport.connect("smtp.gmail.com", "emailtest180115@gmail.com", "wfuynbpmxjylscgo");
//      }
//      if (count % 100 == 0) {
//        System.out.println("Sleeping");
//        Thread.sleep(1000 * 60 * 2);  // 2 mins
//      }
      transport.connect("smtp.gmail.com", "emailtest180115@gmail.com", "wfuynbpmxjylscgo");
//    transport.connect("smtp.gmail.com", "salomon3000@gmail.com", "vswauhzfgslzowmv");
      Thread.sleep(1000 * 2);  // 2 secs
      count++;
      transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
      transport.close();
  }

  public static void sendMailOfAds(String email) throws AddressException, MessagingException {

    // Step1
    System.out.println("\n 1st ===> setup Mail Server Properties..");
    mailServerProperties = System.getProperties();
    mailServerProperties.put("mail.smtp.port", "587");
    mailServerProperties.put("mail.smtp.auth", "true");
    mailServerProperties.put("mail.smtp.starttls.enable", "true");
    System.out.println("Mail Server Properties have been setup successfully..");

    // Step2
    System.out.println("\n\n 2nd ===> get Mail Session..");
    getMailSession = Session.getDefaultInstance(mailServerProperties, null);
    generateMailMessage = new MimeMessage(getMailSession);
    generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
    generateMailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress("nhinguyen130905@gmail.com"));
//    generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@crunchify.com"));

    generateMailMessage.setSubject("Cung cấp dữ liệu (data) khách hàng..");
    String emailBody = "Xin chào Anh/Chị, <br/> "
        + "Em bên cung cấp dữ liệu được <b>cập nhật thường xuyên</b> trên các website bất động sản "
        + "Anh/Chị có nhu cầu chỉ cần phản hồi thư này để lại thông tin, "
        + "bên em sẽ giúp lấy dữ liệu khách hàng cho anh/chị phát triển kế hoạch sale của mình ạ."

        + "<br><br><i> Xin lỗi nếu email này làm phiền quý anh chị<i> <br>"
        + "<br><br> Trân trọng, <br>";
    generateMailMessage.setContent(emailBody, "text/html; charset=utf-8");
    System.out.println("Mail Session has been created successfully..");

    // Step3
    System.out.println("\n\n 3rd ===> Get Session and Send mail");
    Transport transport = getMailSession.getTransport("smtp");

    // Enter your correct gmail UserID and Password
    // if you have 2FA enabled then provide App Specific Password
    transport.connect("smtp.gmail.com", "vanthuc82@gmail.com", "ljkcsnbudkviblaz");
    transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
    transport.close();
  }

  public static void generateAndSendEmail() throws AddressException, MessagingException {

    // Step1
    System.out.println("\n 1st ===> setup Mail Server Properties..");
    mailServerProperties = System.getProperties();
    mailServerProperties.put("mail.smtp.port", "587");
    mailServerProperties.put("mail.smtp.auth", "true");
    mailServerProperties.put("mail.smtp.starttls.enable", "true");
    System.out.println("Mail Server Properties have been setup successfully..");

    // Step2
    System.out.println("\n\n 2nd ===> get Mail Session..");
    getMailSession = Session.getDefaultInstance(mailServerProperties, null);
    generateMailMessage = new MimeMessage(getMailSession);
    generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("hoanhhao@gmail.com"));
//    generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@crunchify.com"));

    generateMailMessage.setSubject("Greetings from Crunchify..");
    String emailBody = "Test email by Crunchify.com JavaMail API example. " + "<br><br> Regards, <br>Crunchify Admin";
    generateMailMessage.setContent(emailBody, "text/html");
    System.out.println("Mail Session has been created successfully..");

    // Step3
    System.out.println("\n\n 3rd ===> Get Session and Send mail");
    Transport transport = getMailSession.getTransport("smtp");

    // Enter your correct gmail UserID and Password
    // if you have 2FA enabled then provide App Specific Password
    transport.connect("smtp.gmail.com", "vanthuc82@gmail.com", "ljkcsnbudkviblaz");
    transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
    transport.close();
  }
}
