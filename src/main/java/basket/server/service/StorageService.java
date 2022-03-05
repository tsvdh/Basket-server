package basket.server.service;

import basket.server.dao.storage.StorageDAO;
import basket.server.util.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StorageService {

    public static final String TYPE_FOLDER = "application/vnd.google-apps.folder";
    public static final String TYPE_ZIP = "application/zip";
    public static final String TYPE_PNG = "application/png";
    public static final String TYPE_TEXT = "text/plain";

    private final StorageDAO storageDAO;

    @Autowired
    public StorageService(@Qualifier("driveStorageDAO") StorageDAO storageDAO) {
        this.storageDAO = storageDAO;
    }

    public void create(String appName) throws IOException {
        log.info("Creating storage for {}", appName);

        boolean success = storageDAO.create(appName);
        if (!success) {
            throw new BadRequestException("Storage creation failed, app already exists");
        }
    }

    public void upload(String appName, InputStream inputStream, String fileName, String fileType) throws IOException {
        log.info("Uploading {} for app {}", fileName, appName);

        boolean success = storageDAO.upload(appName, inputStream, fileName, fileType);
        if (!success) {
            throw new BadRequestException("Could not upload the file, app does not exist");
        }
    }

    public InputStream download(String appName, String fileName, String fileType) throws IOException {
        log.info("Downloading {} for app {}", fileName, appName);

        Optional<InputStream> optionalStream = storageDAO.download(appName, fileName, fileType);
        if (optionalStream.isPresent()) {
            return optionalStream.get();
        } else {
            throw new BadRequestException("App or file does not exist");
        }
    }
}
