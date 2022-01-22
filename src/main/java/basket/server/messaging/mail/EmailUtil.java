package basket.server.messaging.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final static String SEND_ADDRESS = "basket.noreply@gmail.com";

    private final MailSender mailSender;

    public void sendVerificationEmail(String emailAddress) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(SEND_ADDRESS);
        mail.setTo(emailAddress);
        mail.setSubject("Verification code");
        mail.setText("Hello world!");
        mailSender.send(mail);
    }
}
