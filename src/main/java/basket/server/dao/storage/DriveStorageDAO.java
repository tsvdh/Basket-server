package basket.server.dao.storage;

import basket.server.util.IllegalActionException;
import basket.server.util.storage.FileType;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository("driveStorageDAO")
public class DriveStorageDAO implements StorageDAO {

    @Value("#{storageProperties.projectId}")
    private String projectId;

    @Value("#{storageProperties.privateKeyId}")
    private String privateKeyId;

    @Value("#{storageProperties.privateKeyString}")
    private String privateKeyString;

    @Value("#{storageProperties.clientEmail}")
    private String clientEmail;

    @Value("#{storageProperties.clientId}")
    private String clientId;

    @Value("#{storageProperties.tokenServerUri}")
    private String tokenServerUri;

    @Getter
    private Drive drive;

    @PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(privateKeyString);

        var keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);

        var privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        var credentials = ServiceAccountCredentials.newBuilder()
                .setProjectId(projectId)
                .setPrivateKeyId(privateKeyId)
                .setPrivateKey(privateKey)
                .setClientEmail(clientEmail)
                .setClientId(clientId)
                .setTokenServerUri(URI.create(tokenServerUri))
                .setScopes(List.of(DriveScopes.DRIVE))
                .build();

        var credentialsAdapter = new HttpCredentialsAdapter(credentials);

        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var jsonFactory = GsonFactory.getDefaultInstance();

        this.drive = new Drive.Builder(httpTransport, jsonFactory, credentialsAdapter)
                .setApplicationName("Basket cloud")
                .build();
    }

    private Optional<File> getFolder(String appName) throws IOException {
        String query = "name = '%s' and mimeType = '%s'".formatted(appName, FileType.FOLDER);

        return drive.files().list()
                .setQ(query)
                .execute()

                .getFiles()
                .stream().findFirst();
    }

    private Optional<File> getFile(String appName, String fileName) throws IOException {
        Optional<File> optionalFolder = getFolder(appName);
        String folderId;

        if (optionalFolder.isPresent()) {
            folderId = optionalFolder.get().getId();
        } else {
            return Optional.empty();
        }

        String query = "'%s' in parents and name = '%s'".formatted(folderId, fileName);

        return drive.files().list()
                .setQ(query)
                .execute()

                .getFiles()
                .stream().findFirst();
    }

    @Override
    public InputStream download(String appName, String fileName) throws IOException, IllegalActionException {
        Optional<File> optionalFile = getFile(appName, fileName);

        if (optionalFile.isEmpty()) {
            throw new IllegalActionException("File does not exist");
        }

        String id = optionalFile.get().getId();
        return drive.files().get(id).executeMediaAsInputStream();
    }

    @Override
    public void create(String appName) throws IOException, IllegalActionException {
        var newFolder = new File()
                .setName(appName)
                .setMimeType(FileType.FOLDER);

        Optional<File> optionalFolder = getFolder(appName);

        if (optionalFolder.isPresent()) {
            throw new IllegalActionException("App already exists");
        } else {
            drive.files().create(newFolder).execute();
        }
    }

    @Override
    public void delete(String appName) throws IOException, IllegalActionException {
        Optional<File> optionalFolder = getFolder(appName);

        if (optionalFolder.isEmpty()) {
            throw new IllegalActionException("App does not exist");
        }

        String folderId = optionalFolder.get().getId();

        String query = "'%s' in parents".formatted(folderId);

        List<File> files = drive.files().list()
                .setQ(query)
                .execute()
                .getFiles();

        for (File file : files) {
            drive.files().delete(file.getId()).execute();
        }

        drive.files().delete(folderId).execute();
    }

    @Override
    public void upload(String appName, InputStream inputStream, String fileName, String fileType) throws IOException, IllegalActionException {
        if (getFile(appName, fileName).isPresent()) {
            throw new IllegalActionException("File already exists");
        }

        Optional<File> optionalFolder = getFolder(appName);
        String folderId;

        if (optionalFolder.isPresent()) {
            folderId = optionalFolder.get().getId();
        } else {
            throw new IllegalActionException("App does not exist");
        }

        var newFile = new File()
            .setName(fileName)
            .setParents(List.of(folderId));

        var content = new InputStreamContent(fileType, inputStream);

        drive.files().create(newFile, content).execute();
    }

    @Override
    public void rename(String appName, String oldName, String newName) throws IOException, IllegalActionException {
        Optional<File> optionalFile = getFile(appName, oldName);

        if (optionalFile.isEmpty()) {
            throw new IllegalActionException("File does not exist");
        }

        File file = optionalFile.get();

        String id = file.getId();
        var newFile = file.setName(newName);

        drive.files().update(id, newFile).execute();
    }

    @Override
    public void delete(String appName, String fileName) throws IOException, IllegalActionException {
        Optional<File> optionalFile = getFile(appName, fileName);

        if (optionalFile.isEmpty()) {
            throw new IllegalActionException("File does not exist");
        }

        drive.files().delete(optionalFile.get().getId()).execute();
    }

    @Override
    public boolean exists(String appName, String fileName) throws IOException {
        return getFile(appName, fileName).isPresent();
    }
}
