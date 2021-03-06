package basket.server.model.app;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@Document(collection = "apps")
public class App implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String description;

    private String admin;

    private HashSet<String> developers;

    private AppStats appStats;

    private boolean available;

    private Release stable;

    private Release experimental;

    public App(@BsonProperty String name,
               @BsonProperty String description,
               @BsonProperty String admin,
               @BsonProperty HashSet<String> developers,
               @BsonProperty AppStats appStats,
               @BsonProperty boolean available,
               @BsonProperty Release stable,
               @BsonProperty Release experimental) {
        this.name = name;
        this.description = description;
        this.admin = admin;
        this.developers = developers;
        this.appStats = appStats;
        this.available = available;
        this.stable = stable;
        this.experimental = experimental;
    }
}
