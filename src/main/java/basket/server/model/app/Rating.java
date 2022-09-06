package basket.server.model.app;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
public class Rating implements Serializable {

    @Serial
    private static final long serialVersionUID = 5;

    private Float grade;

    private HashMap<String, Integer> reviews;

    public Rating(@BsonProperty Float grade,
                  @BsonProperty HashMap<String, Integer> reviews) {
        this.grade = grade;
        this.reviews = reviews;
    }
}
