package basket.server.model.app;

import basket.server.model.expiring.PendingUpload;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
public class Release implements Serializable {

    @Serial
    private static final long serialVersionUID = 6;

    public enum Type {
        STABLE, EXPERIMENTAL
    }

    private String version;

    private LocalDate date;

    private Type type;

    public Release(PendingUpload pendingUpload) {
        this(
                pendingUpload.getVersion(),
                pendingUpload.getCreatedAt().toLocalDate(),
                Type.valueOf(pendingUpload.getType().toUpperCase())
        );
    }

    public Release(@BsonProperty String version,
                   @BsonProperty LocalDate date,
                   @BsonProperty Type type) {
        this.version = version;
        this.date = date;
        this.type = type;
    }
}
