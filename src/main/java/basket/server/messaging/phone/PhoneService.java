package basket.server.messaging.phone;

import basket.server.model.VerificationCode;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PhoneService {

    public static String phoneToString(PhoneNumber phoneNumber) {
        return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberFormat.E164);
    }

    private static com.twilio.type.PhoneNumber phoneToPhone(PhoneNumber phoneNumber) {
        return new com.twilio.type.PhoneNumber(phoneToString(phoneNumber));
    }

    @Value("#{twilioProperties.accountSid}")
    private String accountSid;

    @Value("#{twilioProperties.authToken}")
    private String authToken;

    @Value("#{twilioProperties.sendPhoneNumber}")
    private String sendPhoneNumber;

    private com.twilio.type.PhoneNumber getSendPhoneNumber() {
        try {
            PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(sendPhoneNumber, "US");
            return phoneToPhone(phoneNumber);
        }
        catch (NumberParseException e) {
            log.error("Unexpected error", e);
            throw new RuntimeException(e);
        }
    }

    public void sendVerificationSMS(VerificationCode verificationCode) {
        Twilio.init(accountSid, authToken);
        Message.creator(phoneToPhone(phoneNumber), getSendPhoneNumber(), "Hello world!").createAsync();
    }
}
