package basket.server.model.expiring;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "pending uploads")
public class PendingUpload implements Serializable {

    @Serial
    private static final long serialVersionUID = 4;

    @Id
    private String id;

    @Indexed(unique = true, expireAfterSeconds = 15 * 60)
    private final String token;

    @Indexed(unique = true)
    private final String destination;

    private final String appName;

    private final String type;

    private final String version;

    private final LocalDateTime createdAt;

    public PendingUpload(String appName, String type, String version) {
        this(
                RandomStringUtils.randomAlphanumeric(10),
                appName + "/" + type,
                appName,
                type,
                version,
                LocalDateTime.now()
        );
    }

    public PendingUpload(@BsonProperty String token,
                         @BsonProperty String destination,
                         @BsonProperty String appName,
                         @BsonProperty String type,
                         @BsonProperty String version,
                         @BsonProperty LocalDateTime createdAt) {
        this.token = token;
        this.destination = destination;
        this.appName = appName;
        this.type = type;
        this.version = version;
        this.createdAt = createdAt;
    }
}
