package basket.server.dao.database.upload;

import basket.server.model.expiring.PendingUpload;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Repository("localPendingUploadDAO")
public class LocalPendingUploadDAO implements PendingUploadDAO {

    private static final ArrayList<PendingUpload> localDB = new ArrayList<>();

    @Override
    public Optional<PendingUpload> getByDestination(String destination) {
        return localDB.stream()
                .filter(pendingUpload -> pendingUpload.getDestination().equals(destination))
                .findFirst();
    }

    @Override
    public Optional<PendingUpload> getByToken(String token) {
        return localDB.stream()
                .filter(pendingUpload -> pendingUpload.getToken().equals(token))
                .findFirst();
    }

    @Override
    public void add(PendingUpload newUpload) {
        // ID of new upload is generated by the database
        newUpload.setId(UUID.randomUUID().toString());

        getByDestination(newUpload.getDestination()).ifPresent(localDB::remove);

        localDB.add(newUpload);
    }

    /**
     * Mimic automatic database clean-up
     */
    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    void removeExpired() {
        var now = OffsetDateTime.now();
        localDB.removeIf(pendingUpload -> pendingUpload.getCreatedAt().plusMinutes(15).isBefore(now));
    }
}
