package basket.server.dao.storage;

import basket.server.util.IllegalActionException;
import java.io.IOException;
import java.io.InputStream;

public interface StorageDAO {

    InputStream download(String appId, String fileName) throws IOException, IllegalActionException;

    long getSize(String appId, String fileName) throws IOException, IllegalActionException;

    void create(String appId) throws IOException, IllegalActionException;

    void upload(String appId, InputStream inputStream, String fileName, String fileType) throws IOException, IllegalActionException, InterruptedException;

    void rename(String appId, String oldName, String newName) throws IOException, IllegalActionException;

    void delete(String appId, String fileName) throws IOException, IllegalActionException;

    void delete(String appId) throws IOException, IllegalActionException;

    boolean exists(String appId, String fileName) throws IOException;
}
