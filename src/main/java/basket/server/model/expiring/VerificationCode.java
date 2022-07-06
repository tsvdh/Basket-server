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
@Document(collection = "verification codes")
public class VerificationCode implements Serializable {

    @Serial
    private static final long serialVersionUID = 3;

    @Id
    private String id;

    @Indexed(unique = true, expireAfterSeconds = 15 * 60)
    private final String address;

    private final String code;

    private final LocalDateTime createdAt;

    public VerificationCode(String address) {
        this(
                address,
                RandomStringUtils.randomAlphabetic(4),
                LocalDateTime.now()
        );
    }

    public VerificationCode(@BsonProperty String address,
                            @BsonProperty String code,
                            @BsonProperty LocalDateTime createdAt) {
        this.address = address;
        this.code = code;
        this.createdAt = createdAt;
    }
}
