package basket.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.net.URL;
import lombok.Data;
import lombok.NonNull;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "apps")
public class App implements Serializable {

    private @Id String id;
    private @Indexed(unique = true) String name;
    private String description;
    private String stable;
    private String experimental;
    private URL iconAddress;
    private URL githubHome;

    @Serial
    private static final long serialVersionUID = 2;

    public App(@BsonProperty @NonNull String name,
               @BsonProperty @NonNull String description,
               @BsonProperty @NonNull String stable,
               @BsonProperty @NonNull String experimental,
               @BsonProperty @NonNull URL iconAddress,
               @BsonProperty @NonNull URL githubHome) {
        this.name = name;
        this.description = description;
        this.stable = stable;
        this.experimental = experimental;
        this.iconAddress = iconAddress;
        this.githubHome = githubHome;
    }
}
