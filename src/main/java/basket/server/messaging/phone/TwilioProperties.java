package basket.server.messaging.phone;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "twilio")
public class TwilioProperties {

    private String accountSid;

    private String authToken;

    private String sendPhoneNumber;
}
