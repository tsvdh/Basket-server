package basket.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
public class AppStats implements Serializable {

    @Serial
    private static final long serialVersionUID = 4;

    private Set<String> users;

    private Rating rating;

    public AppStats(@BsonProperty Set<String> users,
                    @BsonProperty Rating rating) {
        this.users = users;
        this.rating = rating;
    }
}
