package basket.server.service.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "google.drive.service-account")
public class StorageProperties {

    private String projectId;

    private String privateKeyId;

    private String privateKeyString;

    private String clientEmail;

    private String clientId;

    private String tokenServerUri;
}
