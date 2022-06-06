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
        this.busyMap = new HashMap<>();
        this.pendingSet = new HashSet<>();
    }

    private static String toFullName(String appName, String fileName) {
        return appName + "/" + fileName;
    }

    public static String toTempFileName(String fileName) {
        return "temp_" + fileName;
    }

    public void create(String appName) throws IllegalActionException, IOException {
        log.info("Creating storage for pageApp '{}'", appName);
        storageDAO.create(appName);
    }

    public void upload(String appName, InputStream inputStream, String fileName, String fileType) throws IllegalActionException, IOException, InterruptedException {
        log.info("Uploading '{}' for pageApp '{}'", fileName, appName);

        String fullName = toFullName(appName, fileName);
        String tempFileName = toTempFileName(fileName);

        // initial upload
        if (!storageDAO.exists(appName, fileName)) {
            storageDAO.upload(appName, inputStream, fileName, fileType);

            busyMap.put(fullName, 0);
            return;
        }

        // delete pending temp file
        if (storageDAO.exists(appName, tempFileName)) {
            storageDAO.delete(appName, tempFileName);
        }

        // upload new temp file
        storageDAO.upload(appName, inputStream, tempFileName, fileType);

        // if file is in use, wait. else, execute update
        if (busyMap.get(fullName) > 0) {
            pendingSet.add(fullName);
        } else {
            storageDAO.delete(appName, fileName);
            storageDAO.rename(appName, tempFileName, fileName);
        }
    }

    public boolean isReleasable(String appName) throws IOException {
        log.info("Checking if '{}' is complete", appName);

        return storageDAO.exists(appName, FileName.ICON)
                && storageDAO.exists(appName, FileName.STABLE);
    }

    public boolean exists(String appName, String fileName) throws IOException {
        log.info("Checking if '{}' of '{}' exists", fileName, appName);

        return storageDAO.exists(appName, fileName);
    }

    public Optional<InputStream> download(String appName, String fileName) throws IllegalActionException, IOException {
        log.info("Downloading '{}' of pageApp '{}'", fileName, appName);

        InputStream inputStream = storageDAO.download(appName, fileName);

        String fullName = toFullName(appName, fileName);

        if (pendingSet.contains(fullName)) {
            return Optional.empty();
        } else {
            busyMap.replace(fullName, busyMap.get(fullName) + 1);
        }

        return Optional.of(inputStream);
    }

    public void endDownload(String appName, String fileName) throws IOException {
        log.info("Handling end of '{}' download of pageApp '{}'", fileName, appName);

        String fullName = toFullName(appName, fileName);

        busyMap.replace(fullName, busyMap.get(fullName) - 1);

        if (busyMap.get(fullName) == 0 && pendingSet.contains(fullName)) {
            try {
                storageDAO.delete(appName, fileName);
                storageDAO.rename(appName, toTempFileName(fileName), fileName);

                pendingSet.remove(fullName);
            }
            catch (IllegalActionException ignored) {}
        }
    }
}
