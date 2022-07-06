package basket.server.service;

import basket.server.dao.database.upload.PendingUploadDAO;
import basket.server.model.expiring.PendingUpload;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PendingUploadService {

    private final PendingUploadDAO pendingUploadDAO;

    @Autowired
    public PendingUploadService(@Qualifier("localPendingUploadDAO") PendingUploadDAO pendingUploadDAO) {
        this.pendingUploadDAO = pendingUploadDAO;
    }

    public Optional<PendingUpload> getByDestination(String destination) {
        log.info("Getting pending upload for destination '{}'", destination);

        return pendingUploadDAO.getByDestination(destination);
    }

    public Optional<PendingUpload> getByToken(String token) {
        log.info("Getting pending upload for token '{}'", token);

        return pendingUploadDAO.getByToken(token);
    }

    public void add(PendingUpload pendingUpload) {
        log.info("Adding pending upload for '{}'", pendingUpload.getDestination());

        pendingUploadDAO.add(pendingUpload);
    }
}
