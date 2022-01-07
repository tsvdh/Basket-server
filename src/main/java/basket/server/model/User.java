package basket.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

@Data
@Document(collection = "users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 2;

    @Id
    private String id;

    @NotNull @Email @Indexed(unique = true)
    private String email;

    @NotBlank @Indexed(unique = true)
    private String username;

    @NotBlank
    private String encodedPassword;

    @NotNull
    private Set<String> userOf;

    @NotNull
    private boolean developer;

    @Nullable @Valid
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
