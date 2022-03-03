package basket.server.service;

import basket.server.model.VerificationCode;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PhoneService {

    public static String phoneToString(PhoneNumber phoneNumber) {
        return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberFormat.E164);
    }

    @Value("#{twilioProperties.accountSid}")
    private String accountSid;

    @Value("#{twilioProperties.authToken}")
    private String authToken;

    @Value("#{twilioProperties.sendPhoneNumber}")
    private String sendPhoneNumberString;

    private com.twilio.type.PhoneNumber sendPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);

        try {
            PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(sendPhoneNumberString, "US");
            this.sendPhoneNumber = new com.twilio.type.PhoneNumber(phoneToString(phoneNumber));
        }
        catch (NumberParseException e) {
            log.error("Unexpected error", e);
            throw new RuntimeException(e);
        }
    }

    public void sendVerificationSMS(VerificationCode verificationCode) {
        Message.creator(
                new com.twilio.type.PhoneNumber(verificationCode.getAddress()),
                sendPhoneNumber,
                "Your verification code for Basket is " + verificationCode.getCode()
        ).createAsync();
    }
}
