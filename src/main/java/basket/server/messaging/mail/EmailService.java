package basket.server.messaging.mail;

import basket.server.model.VerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final MailSender mailSender;

    @Value("${spring.mail.properties.address}")
    private String sendEmailAddress;

    public void sendVerificationEmail(VerificationCode verificationCode) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(sendEmailAddress);
        mail.setTo(emailAddress);
        mail.setSubject("Verification code");
        mail.setText("Hello world!");
        mailSender.send(mail);
    }
}