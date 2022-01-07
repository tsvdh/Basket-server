package basket.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class DeveloperInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3;

    @NotBlank
    private String fullName;

    @NotBlank @Indexed(unique = true)
    private String phoneNumber;

    @NotNull
    private Set<String> developerOf;

    @NotNull
    private Set<String> adminOf;

    public DeveloperInfo(@BsonProperty String fullName,
                         @BsonProperty String phoneNumber,
                         @BsonProperty Set<String> developerOf,
                         @BsonProperty Set<String> adminOf) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.developerOf = developerOf;
        this.adminOf = adminOf;
    }
}
