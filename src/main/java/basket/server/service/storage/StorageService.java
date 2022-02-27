package basket.server.service.storage;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

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

    // var metadata = new File();
    // metadata.setName("test.txt");
    // var content = new FileContent("text/plain", new ClassPathResource("test.txt").getFile());
    //
    // drive.files().update("10gmqWQt-aCHaKDvyNHPFdR1z8rUAS2Je", metadata, content)
    //         .execute();
    //
    // // drive.files().create(metadata, content).execute();
    // // var id = drive.files().list().execute().getFiles().stream().filter(file -> file.getName().equals("test2.txt")).findFirst().get().getId();
    // // drive.files().delete(id).execute();
    //
    // // drive.files().get("10gmqWQt-aCHaKDvyNHPFdR1z8rUAS2Je").executeMediaAndDownloadTo();
    //
    // System.out.println(drive.files().list().execute());
    // System.out.println(drive.about().get().setFields("storageQuota").execute().getStorageQuota());
}
