package basket.server.service;

import basket.server.dao.storage.StorageDAO;
import basket.server.util.IllegalActionException;
import basket.server.util.types.storage.FileName;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StorageService {

    private final StorageDAO storageDAO;

    private final Map<String, Integer> busyMap;
    private final Set<String> pendingSet;

    @Autowired
    public StorageService(@Qualifier("localStorageDAO") StorageDAO storageDAO) {
        this.storageDAO = storageDAO;
        // TODO: auto clean pending
        this.busyMap = new HashMap<>();
        this.pendingSet = new HashSet<>();
    }

    private static String toFullName(String appId, String fileName) {
        return appId + "/" + fileName;
    }

    public static String toTempFileName(String fileName) {
        return "temp_" + fileName;
    }

    public void create(String appId) throws IllegalActionException, IOException {
        log.info("Creating storage for app '{}'", appId);
        storageDAO.create(appId);
    }

    public void upload(String appId, InputStream inputStream, String fileName, String fileType) throws IllegalActionException, IOException, InterruptedException {
        log.info("Uploading '{}' for app '{}'", fileName, appId);

        String fullName = toFullName(appId, fileName);
        String tempFileName = toTempFileName(fileName);

        // initial upload
        if (!storageDAO.exists(appId, fileName)) {
            storageDAO.upload(appId, inputStream, fileName, fileType);

            busyMap.put(fullName, 0);
            return;
        }

        // delete pending temp file
        if (storageDAO.exists(appId, tempFileName)) {
            storageDAO.delete(appId, tempFileName);
        }

        // upload new temp file
        storageDAO.upload(appId, inputStream, tempFileName, fileType);

        // if file is in use, wait. else, execute update
        if (busyMap.get(fullName) > 0) {
            pendingSet.add(fullName);
        } else {
            storageDAO.delete(appId, fileName);
            storageDAO.rename(appId, tempFileName, fileName);
        }
    }

    public long getSize(String appId, String fileName) throws IOException, IllegalActionException {
        log.info("Getting size of {} of {}", fileName, appId);
        return storageDAO.getSize(appId, fileName);
    }

    public boolean isReleasable(String appId) throws IOException {
        log.info("Checking if '{}' is complete", appId);

        return storageDAO.exists(appId, FileName.ICON)
                && storageDAO.exists(appId, FileName.STABLE);
    }

    public boolean exists(String appId, String fileName) throws IOException {
        log.info("Checking if '{}' of '{}' exists", fileName, appId);

        return storageDAO.exists(appId, fileName);
    }

    public Optional<InputStream> download(String appId, String fileName) throws IllegalActionException, IOException {
        log.info("Downloading '{}' of app '{}'", fileName, appId);

        InputStream inputStream = storageDAO.download(appId, fileName);

        String fullName = toFullName(appId, fileName);

        if (pendingSet.contains(fullName)) {
            return Optional.empty();
        } else {
            busyMap.replace(fullName, busyMap.get(fullName) + 1);
        }

        return Optional.of(inputStream);
    }

    public void endDownload(String appId, String fileName) throws IOException {
        log.info("Handling end of '{}' download of app '{}'", fileName, appId);

        String fullName = toFullName(appId, fileName);

        busyMap.replace(fullName, busyMap.get(fullName) - 1);

        if (busyMap.get(fullName) == 0 && pendingSet.contains(fullName)) {
            try {
                storageDAO.delete(appId, fileName);
                storageDAO.rename(appId, toTempFileName(fileName), fileName);

                pendingSet.remove(fullName);
            }
            catch (IllegalActionException ignored) {}
        }
    }
}
