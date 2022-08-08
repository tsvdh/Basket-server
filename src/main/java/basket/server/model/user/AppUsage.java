package basket.server.model.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.OffsetDateTime;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
public class AppUsage implements Serializable {

    @Serial
    private static final long serialVersionUID = 7;

    private Duration timeUsed;

    private OffsetDateTime lastUsed;

    public AppUsage(@BsonProperty Duration timeUsed,
                    @BsonProperty OffsetDateTime lastUsed) {
        this.timeUsed = timeUsed;
        this.lastUsed = lastUsed;
    }
}
