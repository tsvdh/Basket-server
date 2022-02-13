package basket.server.messaging.email;

import basket.server.model.VerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final MailSender mailSender;

    @Value("${spring.mail.properties.address}")
    private String sendEmailAddress;

    public void sendVerificationEmail(VerificationCode verificationCode) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(sendEmailAddress);
        mail.setTo(verificationCode.getAddress());
        mail.setSubject("Verification code");
        mail.setText("Your verification code for Basket is " + verificationCode.getCode());
        // mailSender.send(mail);
        log.info("Code {} for {}", verificationCode.getCode(), verificationCode.getAddress());
    }
}
