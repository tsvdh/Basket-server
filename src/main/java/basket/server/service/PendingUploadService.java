package basket.server.service;

import basket.server.dao.database.upload.PendingUploadDAO;
import basket.server.model.PendingUpload;
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

    public Optional<PendingUpload> get(String destination) {
        log.info("Getting pending upload for '{}'", destination);

        return pendingUploadDAO.get(destination);
    }

    public void add(PendingUpload pendingUpload) {
        log.info("Adding pending upload for '{}'", pendingUpload.getDestination());

        pendingUploadDAO.add(pendingUpload);
    }
}
