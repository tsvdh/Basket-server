package basket.server.dao.storage;

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

    private String getFolderId(String appName) throws IOException {
        String query = "name = %s, mimeType = %s".formatted(appName, TYPE_FOLDER);

        return drive.files().list()
                .setQ(query)
                .execute()

                .getFiles()
                .get(0)
                .getId();
    }

    @Override
    public boolean create(String appName) throws IOException {
        var metadata = new File();
        metadata.setName(appName);
        metadata.setMimeType(TYPE_FOLDER);

        String query = "mimeType = %s".formatted(TYPE_FOLDER);

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

    public void upload(String appName, InputStream inputStream, String fileName, String fileType) throws IOException {
        String query = "%s in parents, name = %s, mimeType = %s".formatted(getFolderId(appName), fileName, fileType);

        List<File> result = drive.files().list()
                .setQ(query)
                .execute()
                .getFiles();

        var metadata = new File();
        metadata.setName("stable_image.zip");

        var content = new InputStreamContent(fileType, inputStream);

        if (result.isEmpty()) {
            drive.files().create(metadata, content);
        } else {
            drive.files().update(result.get(0).getId(), metadata, content).execute();
        }
    }

    public InputStream download(String appName, String fileName, String fileType) throws IOException {
        String query = "%s in parents, name = %s, mimeType = %s".formatted(getFolderId(appName), fileName, fileType);

        String id = drive.files().list()
                .setQ(query)
                .execute()

                .getFiles()
                .get(0)
                .getId();

        return drive.files().get(id).executeMediaAsInputStream();
    }
}
