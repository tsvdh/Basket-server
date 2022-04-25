package basket.server.dao.database.upload;

import basket.server.model.PendingUpload;
import java.util.Optional;

public interface PendingUploadDAO {

    Optional<PendingUpload> getByDestination(String destination);

    Optional<PendingUpload> getByToken(String token);

    void add(PendingUpload pendingUpload);
}
