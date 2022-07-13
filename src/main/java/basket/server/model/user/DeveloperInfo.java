package basket.server.model.user;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
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

    private HashSet<String> developerOf;

    private HashSet<String> adminOf;

    public DeveloperInfo(@BsonProperty String firstName,
                         @BsonProperty String lastName,
                         @BsonProperty PhoneNumber phoneNumber,
                         @BsonProperty HashSet<String> developerOf,
                         @BsonProperty HashSet<String> adminOf) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.developerOf = developerOf;
        this.adminOf = adminOf;
    }
}
