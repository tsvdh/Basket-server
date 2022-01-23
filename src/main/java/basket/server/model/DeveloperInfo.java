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

    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    private PhoneNumber phoneNumber;

    private Set<String> developerOf;

    private Set<String> adminOf;

    public DeveloperInfo(@BsonProperty String firstName,
                         @BsonProperty String lastName,
                         @BsonProperty PhoneNumber phoneNumber,
                         @BsonProperty Set<String> developerOf,
                         @BsonProperty Set<String> adminOf) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.developerOf = developerOf;
        this.adminOf = adminOf;
    }
}
