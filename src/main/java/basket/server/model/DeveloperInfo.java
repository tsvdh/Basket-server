package basket.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
import lombok.NonNull;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class DeveloperInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3;

    private String fullName;
    private @Indexed(unique = true) String phoneNumber;
    private Set<String> developerOf;
    private Set<String> adminOf;

    public DeveloperInfo(@BsonProperty @NonNull String fullName,
                         @BsonProperty @NonNull String phoneNumber,
                         @BsonProperty @NonNull Set<String> developerOf,
                         @BsonProperty @NonNull Set<String> adminOf) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.developerOf = developerOf;
        this.adminOf = adminOf;
    }
}
