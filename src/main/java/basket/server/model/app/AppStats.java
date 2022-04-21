package basket.server.model.app;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
public class AppStats implements Serializable {

    @Serial
    private static final long serialVersionUID = 4;

    private long amountOfUsers;

    private Rating rating;

    public AppStats(@BsonProperty long amountOfUsers,
                    @BsonProperty Rating rating) {
        this.amountOfUsers = amountOfUsers;
        this.rating = rating;
    }
}
