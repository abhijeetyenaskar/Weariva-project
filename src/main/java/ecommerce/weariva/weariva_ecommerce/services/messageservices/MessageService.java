package ecommerce.weariva.weariva_ecommerce.services.messageservices;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setFrom(new InternetAddress("abhijitconqueror@gmail.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);
            String htmlcontent = "<div>" +
                    "<h3 style='font-weight:bold;'>OTP for Reset Password from Weariva Application Team</h3>" +
                    body +
                    "<p>Thank You!!</p>" +
                    "</div>";
            message.setContent(htmlcontent, "text/html;charset=utf-8");
            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println("Error in Message Sending...");
        }
    }

}