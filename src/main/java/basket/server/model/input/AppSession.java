package basket.server.model.input;

import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class AppSession {

    private OffsetDateTime start;
    private OffsetDateTime end;
}