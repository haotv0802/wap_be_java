package wap.api.rest.crawling.mailing;

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

  public static void main(String args[]) throws AddressException, MessagingException {
//    generateAndSendEmail();
    sendMailOfAds("hoanhhao@gmail.com");
    System.out.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
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
