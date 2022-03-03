package basket.server.service;

import basket.server.dao.storage.StorageDAO;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StorageService {

    private final StorageDAO storageDAO;

    @Autowired
    public StorageService(@Qualifier("driveStorageDAO") StorageDAO storageDAO) {
        this.storageDAO = storageDAO;
    }

    void upload(String appName, InputStream inputStream, String fileName, String fileType) throws IOException {
        log.info("Uploading {} for app {}", fileName, appName);
        storageDAO.upload(appName, inputStream, fileName, fileType);
    }

    InputStream download(String appName, String fileName, String fileType) throws IOException {
        log.info("Downloading {} for app {}", fileName, appName);
        return storageDAO.download(appName, fileName, fileType);
    }
}
