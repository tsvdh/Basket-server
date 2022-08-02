package basket.server.controller.api;

import basket.server.model.app.Release;
import basket.server.model.input.FormPendingUpload;
import basket.server.service.AppService;
import basket.server.service.PendingUploadService;
import basket.server.service.StorageService;
import basket.server.service.ValidationService;
import basket.server.util.IllegalActionException;
import basket.server.util.types.storage.FileName;
import basket.server.util.types.storage.FileType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/storage")
@Validated
public class StorageController {

    private final StorageService storageService;
    private final PendingUploadService pendingUploadService;
    private final ValidationService validationService;
    private final AppService appService;

    @PostMapping("upload/{token}")
    public ResponseEntity<Void> upload(HttpServletRequest request, @PathVariable String token) throws IOException, FileUploadException {

        var optionalPendingUpload = pendingUploadService.getByToken(token);
        if (optionalPendingUpload.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid token");
        }

        var pendingUpload = optionalPendingUpload.get();

        String fileName;
        String fileType;

        switch (pendingUpload.getType()) {
            case "icon" -> {
                fileName = FileName.ICON;
                fileType = FileType.PNG;
            }
            case "stable" -> {
                fileName = FileName.STABLE;
                fileType = FileType.ZIP;
            }
            case "experimental" -> {
                fileName = FileName.EXPERIMENTAL;
                fileType = FileType.ZIP;
            }
            default -> throw new HttpClientErrorException(
                    HttpStatus.NOT_FOUND,
                    "'%s' upload type does not exist".formatted(pendingUpload.getType())
            );
        }

        var upload = new ServletFileUpload();
        FileItemIterator iterator = upload.getItemIterator(request);

        InputStream inputStream;

        if (iterator.hasNext()) {
            FileItemStream file = iterator.next();

            if (!file.isFormField()) {
                if (file.getContentType().equals(fileType)) {
                    inputStream = file.openStream();
                } else {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Wrong file type");
                }
            } else {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Wrong request format");
            }
        } else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Wrong request format");
        }

        try {
            storageService.upload(pendingUpload.getAppId(), inputStream, fileName, fileType);
        } catch (IllegalActionException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var optionalApp = appService.getById(pendingUpload.getAppId());
        if (optionalApp.isEmpty()) {
            // should not happen as token guarantees app existence
            return ResponseEntity.internalServerError().build();
        }

        var app = optionalApp.get();
        var release = new Release(pendingUpload);

        switch (pendingUpload.getType()) {
            case "stable" -> app.setStable(release);
            case "experimental" -> app.setExperimental(release);
        }

        return ok().build();
    }

    @PostMapping(path = "upload/init")
    @PreAuthorize("hasRole('DEVELOPER/' + #formPendingUpload.appId)")
    public String initUpload(@ModelAttribute FormPendingUpload formPendingUpload) {

        var pendingUpload = validationService.validate(formPendingUpload);

        pendingUploadService.add(pendingUpload);

        return "fragments/elements/app/releases :: file-upload (type = '%s', token = '%s')"
                .formatted(pendingUpload.getType(), pendingUpload.getToken());
    }

    @GetMapping("download")
    public ResponseEntity<StreamingResponseBody> download(@RequestParam String appId, @RequestParam String fileName) throws IOException {
        Optional<InputStream> optionalStream;
        try {
            optionalStream = storageService.download(appId, fileName);
        } catch (IllegalActionException e) {
            return badRequest().build();
        }

        if (optionalStream.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .build();
        }

        InputStream inputStream = optionalStream.get();
        StreamingResponseBody responseBody = outputStream -> {
            inputStream.transferTo(outputStream);
            inputStream.close();
        };

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    @PostMapping("download/end") // TODO: secure access
    public ResponseEntity<Void> endDownload(@RequestParam String appId, @RequestParam String fileName) throws IOException {
        storageService.endDownload(appId, fileName);
        return ok().build();
    }

    @GetMapping(path = "html/status")
    @PreAuthorize("hasRole('DEVELOPER/' + #appId)")
    public String getStorageStatus(@RequestParam String appId, Model model) {
        var optionalApp = appService.getById(appId);
        if (optionalApp.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "App does not exist");
        }

        model.addAttribute("pageApp", optionalApp.get());
        model.addAttribute("storageService", storageService);

        return "fragments/elements/app/releases :: storage-status";
    }
}
