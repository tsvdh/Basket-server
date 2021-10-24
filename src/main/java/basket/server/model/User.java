package basket.server.model;

import com.mongodb.lang.Nullable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
import lombok.NonNull;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

@Data
@Document(collection = "users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 2;

    private @Id String id;
    private @Indexed(unique = true) String email;
    private @Indexed(unique = true) String username;
    private String encodedPassword;
    private Set<String> userOf;
    private boolean developer;
    private DeveloperInfo developerInfo;

    public User(@BsonProperty @NonNull String email,
                @BsonProperty @NonNull String username,
                @BsonProperty @NonNull String encodedPassword,
                @BsonProperty @NonNull Set<String> userOf,
                @BsonProperty @NonNull boolean developer,
                @BsonProperty @Nullable @Validated DeveloperInfo developerInfo) {
        this.email = email;
        this.username = username;
        this.encodedPassword = encodedPassword;
        this.userOf = userOf;
        this.developer = developer;
        this.developerInfo = developerInfo;
    }
}
