package basket.server.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
public class Rating implements Serializable {

    @Serial
    private static final long serialVersionUID = 5;

    private long amount;

    private float grade;

    public Rating(@BsonProperty long amount,
                  @BsonProperty float grade) {
        this.amount = amount;
        this.grade = grade;
    }
}
