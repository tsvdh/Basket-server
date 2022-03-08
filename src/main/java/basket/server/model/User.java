package basket.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 2;

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String username;

    private String encodedPassword;

    private Set<String> userOf;

    private boolean developer;

    private DeveloperInfo developerInfo;

    public User(@BsonProperty String email,
                @BsonProperty String username,
                @BsonProperty String encodedPassword,
                @BsonProperty Set<String> userOf,
                @BsonProperty boolean developer,
                @BsonProperty DeveloperInfo developerInfo) {
        this.email = email;
        this.username = username;
        this.encodedPassword = encodedPassword;
        this.userOf = userOf;
        this.developer = developer;
        this.developerInfo = developerInfo;
    }
}
