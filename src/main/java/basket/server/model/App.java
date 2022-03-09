package basket.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "apps")
public class App implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String description;

    private Release stable;

    private Release experimental;

    private String admin;

    private Set<String> developers;

    private AppStats appStats;

    public App(@BsonProperty String name,
               @BsonProperty String description,
               @BsonProperty Release stable,
               @BsonProperty Release experimental,
               @BsonProperty String admin,
               @BsonProperty Set<String> developers,
               @BsonProperty AppStats appStats) {
        this.name = name;
        this.description = description;
        this.stable = stable;
        this.experimental = experimental;
        this.admin = admin;
        this.developers = developers;
        this.appStats = appStats;
    }
}
