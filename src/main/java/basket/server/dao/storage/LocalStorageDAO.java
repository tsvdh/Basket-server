package basket.server.dao.storage;

import basket.server.util.IllegalActionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Repository;

@Repository("localStorageDAO")
public class LocalStorageDAO implements StorageDAO {

    private static final Map<String, Map<String, File>> files = new HashMap<>();

    private static class File {

        private byte[] content;

        private InputStream getContent() {
            return new ByteArrayInputStream(content);
        }

        private void loadContent(InputStream inputStream) throws IOException {
            content = IOUtils.toByteArray(inputStream);
        }
    }

    private Optional<Map<String, File>> getFolder(String appId) {
        return Optional.ofNullable(files.get(appId));
    }

    private Optional<File> getFile(String appId, String fileName) {
        var optionalFolder = getFolder(appId);
        if (optionalFolder.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(optionalFolder.get().get(fileName));
    }

    @Override
    public InputStream download(String appId, String fileName) throws IllegalActionException {
        var optionalFile = getFile(appId, fileName);
        if (optionalFile.isEmpty()) {
            throw new IllegalActionException("App or file does not exist");
        }
        return optionalFile.get().getContent();
    }

    @Override
    public long getSize(String appId, String fileName) throws IOException, IllegalActionException {
        var optionalFile = getFile(appId, fileName);
        if (optionalFile.isEmpty()) {
            throw new IllegalActionException("App or file does not exist");
        }
        return optionalFile.get().content.length;
    }

    @Override
    public void create(String appId) throws IOException, IllegalActionException {
        if (files.containsKey(appId)) {
            throw new IllegalActionException("App already exists");
        } else {
            files.put(appId, new HashMap<>());
        }
    }

    @Override
    public void upload(String appId, InputStream inputStream, String fileName, String fileType) throws IOException, IllegalActionException, InterruptedException {
        var optionalFolder = getFolder(appId);
        if (optionalFolder.isEmpty()) {
            throw new IllegalActionException("App does not exist");
        }

        var folder = optionalFolder.get();

        if (folder.get(fileName) != null) {
            throw new IllegalActionException("File already exists");
        }

        var file = new File();
        try {
            file.loadContent(inputStream);
        } catch (IOException e) {
            throw new InterruptedException("File upload not completed");
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        folder.put(fileName, file);
    }

    @Override
    public void rename(String appId, String oldName, String newName) throws IllegalActionException {
        var optionalFile = getFile(appId, oldName);
        if (optionalFile.isEmpty()) {
            throw new IllegalActionException("App or file does not exist");
        }

        var folder = files.get(appId);
        folder.remove(oldName);
        folder.put(newName, optionalFile.get());
    }

    @Override
    public void delete(String appId, String fileName) throws IllegalActionException {
        var file = getFile(appId, fileName);
        if (file.isEmpty()) {
            throw new IllegalActionException("App or file does not exist");
        }

        files.get(appId).remove(fileName);
    }

    @Override
    public void delete(String appId) throws IllegalActionException {
        if (files.get(appId) == null) {
            throw new IllegalActionException("App does not exist");
        } else {
            files.remove(appId);
        }
    }

    @Override
    public boolean exists(String appId, String fileName) {
        return getFile(appId, fileName).isPresent();
    }
}
