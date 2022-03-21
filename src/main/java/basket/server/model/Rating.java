package basket.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
public class Rating implements Serializable {

    @Serial
    private static final long serialVersionUID = 5;

    private float grade;

    private Map<String, Integer> reviews;

    public Rating(@BsonProperty float grade,
                  @BsonProperty Map<String, Integer> reviews) {
        this.grade = grade;
        this.reviews = reviews;
    }
}
