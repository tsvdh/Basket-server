package basket.server.dao.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface StorageDAO {

    boolean create(String appName) throws IOException;

    boolean upload(String appName, InputStream inputStream, String fileName, String fileType) throws IOException;

    Optional<InputStream> download(String appName, String fileName, String fileType) throws IOException;
}
