package basket.server.dao.database.upload;

import basket.server.model.PendingUpload;
import java.util.Optional;

public interface PendingUploadDAO {

    Optional<PendingUpload> get(String destination);

    void add(PendingUpload pendingUpload);
}
