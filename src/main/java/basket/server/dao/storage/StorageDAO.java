package basket.server.dao.storage;

import java.io.IOException;
import java.io.InputStream;

public interface StorageDAO {

    String TYPE_FOLDER = "application/vnd.google-apps.folder";
    String TYPE_ZIP = "application/zip";
    String TYPE_PNG = "application/png";

    boolean create(String appName) throws IOException;

    void upload(String appName, InputStream inputStream, String fileName, String fileType) throws IOException;

    InputStream download(String appName, String fileName, String fileType) throws IOException;
}
