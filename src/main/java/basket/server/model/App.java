package basket.server.model;

import basket.api.util.Version;
import java.io.Serial;
import java.io.Serializable;
import java.net.URL;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "apps")
public class App implements Serializable {

    @Id String id;
    @Indexed(unique = true) String name;
    String description;
    Version stable;
    Version experimental;
    URL iconAddress;
    URL githubHome;

    @Serial
    private static final long serialVersionUID = 2;

    public App(@BsonProperty String name,
               @BsonProperty String description,
               @BsonProperty Version stable,
               @BsonProperty Version experimental,
               @BsonProperty URL iconAddress,
               @BsonProperty URL githubHome) {
        this.name = name;
        this.description = description;
        this.stable = stable;
        this.experimental = experimental;
        this.iconAddress = iconAddress;
        this.githubHome = githubHome;
    }
}
