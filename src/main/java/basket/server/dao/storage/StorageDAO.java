package basket.server.dao.storage;

import basket.server.util.IllegalActionException;
import java.io.IOException;
import java.io.InputStream;

public interface StorageDAO {

    InputStream download(String appName, String fileName) throws IOException, IllegalActionException;

    void create(String appName) throws IOException, IllegalActionException;

    void upload(String appName, InputStream inputStream, String fileName, String fileType) throws IOException, IllegalActionException;

    void rename(String appName, String oldName, String newName) throws IOException, IllegalActionException;

    void delete(String appName, String fileName) throws IOException, IllegalActionException;

    void delete(String appName) throws IOException, IllegalActionException;

    boolean exists(String appName, String fileName) throws IOException;
}
