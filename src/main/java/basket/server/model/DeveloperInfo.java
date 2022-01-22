package basket.server.model;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class DeveloperInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3;

    private String fullName;

    @Indexed(unique = true)
    private PhoneNumber phoneNumber;

    private Set<String> developerOf;

    private Set<String> adminOf;

    public DeveloperInfo(@BsonProperty String fullName,
                         @BsonProperty PhoneNumber phoneNumber,
                         @BsonProperty Set<String> developerOf,
                         @BsonProperty Set<String> adminOf) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.developerOf = developerOf;
        this.adminOf = adminOf;
    }
}
