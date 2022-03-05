package basket.server.dao.storage;

import basket.server.service.StorageService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
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

    private Optional<String> getFolderId(String appName) throws IOException {
        String query = "name = '%s' and mimeType = '%s'".formatted(appName, StorageService.TYPE_FOLDER);

        return drive.files().list()
                .setQ(query)
                .execute()

                .getFiles()
                .stream().findFirst()
                .map(File::getId);
    }

    @Override
    public boolean create(String appName) throws IOException {
        var metadata = new File();
        metadata.setName(appName);
        metadata.setMimeType(StorageService.TYPE_FOLDER);

        String query = "mimeType = '%s'".formatted(StorageService.TYPE_FOLDER);

        FileList fileList = drive.files().list()
                .setQ(query)
                .execute();

        for (File file : fileList.getFiles()) {
            if (file.getName().equals(appName)) {
                return false;
            }
        }

        drive.files().create(metadata).execute();

        return true;
    }

    public boolean upload(String appName, InputStream inputStream, String fileName, String fileType) throws IOException {
        Optional<String> optionalFolderId = getFolderId(appName);
        String folderId;

        if (optionalFolderId.isPresent()) {
            folderId = optionalFolderId.get();
        } else {
            return false;
        }

        String query = "'%s' in parents and name = '%s' and mimeType = '%s'".formatted(folderId, fileName, fileType);

        List<File> result = drive.files().list()
                .setQ(query)
                .execute()
                .getFiles();

        var metadata = new File();
        metadata.setName(fileName);
        metadata.setParents(List.of(folderId));

        var content = new InputStreamContent(fileType, inputStream);

        if (result.isEmpty()) {
            drive.files().create(metadata, content).execute();
        } else {
            drive.files().update(result.get(0).getId(), metadata, content).execute();
        }

        return true;
    }

    public Optional<InputStream> download(String appName, String fileName, String fileType) throws IOException {
        Optional<String> optionalFolderId = getFolderId(appName);
        String folderId;

        if (optionalFolderId.isPresent()) {
            folderId = optionalFolderId.get();
        } else {
            return Optional.empty();
        }

        String query = "'%s' in parents and name = '%s' and mimeType = '%s'".formatted(folderId, fileName, fileType);

        Optional<File> optionalFile = drive.files().list()
                .setQ(query)
                .execute()

                .getFiles()
                .stream().findFirst();

        if (optionalFile.isPresent()) {
            String id = optionalFile.get().getId();
            return Optional.of(drive.files().get(id).executeMediaAsInputStream());
        } else {
            return Optional.empty();
        }
    }
}
