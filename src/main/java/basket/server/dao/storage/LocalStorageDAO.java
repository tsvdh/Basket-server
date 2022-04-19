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

    private Optional<Map<String, File>> getFolder(String appName) {
        return Optional.ofNullable(files.get(appName));
    }

    private Optional<File> getFile(String appName, String fileName) {
        var optionalFolder = getFolder(appName);
        if (optionalFolder.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(optionalFolder.get().get(fileName));
    }

    @Override
    public InputStream download(String appName, String fileName) throws IllegalActionException {
        var optionalFile = getFile(appName, fileName);
        if (optionalFile.isEmpty()) {
            throw new IllegalActionException("App or file does not exist");
        }
        return optionalFile.get().getContent();
    }

    @Override
    public void create(String appName) throws IOException, IllegalActionException {
        if (files.containsKey(appName)) {
            throw new IllegalActionException("App already exists");
        } else {
            files.put(appName, new HashMap<>());
        }
    }

    @Override
    public void upload(String appName, InputStream inputStream, String fileName, String fileType) throws IOException, IllegalActionException {
        var optionalFolder = getFolder(appName);
        if (optionalFolder.isEmpty()) {
            throw new IllegalActionException("App does not exist");
        }

        var folder = optionalFolder.get();

        if (folder.get(fileName) != null) {
            throw new IllegalActionException("File already exists");
        }

        var file = new File();
        file.loadContent(inputStream);

        folder.put(fileName, file);

        inputStream.close();
    }

    @Override
    public void rename(String appName, String oldName, String newName) throws IllegalActionException {
        var optionalFile = getFile(appName, oldName);
        if (optionalFile.isEmpty()) {
            throw new IllegalActionException("App or file does not exist");
        }

        var folder = files.get(appName);
        folder.remove(oldName);
        folder.put(newName, optionalFile.get());
    }

    @Override
    public void delete(String appName, String fileName) throws IllegalActionException {
        var file = getFile(appName, fileName);
        if (file.isEmpty()) {
            throw new IllegalActionException("App or file does not exist");
        }

        files.get(appName).remove(fileName);
    }

    @Override
    public void delete(String appName) throws IllegalActionException {
        if (files.get(appName) == null) {
            throw new IllegalActionException("App does not exist");
        } else {
            files.remove(appName);
        }
    }

    @Override
    public boolean exists(String appName, String fileName) {
        return getFile(appName, fileName).isPresent();
    }
}
