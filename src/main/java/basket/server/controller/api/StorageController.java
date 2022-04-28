package basket.server.controller.api;

import basket.server.model.app.Release;
import basket.server.model.input.FormPendingUpload;
import basket.server.service.AppService;
import basket.server.service.PendingUploadService;
import basket.server.service.StorageService;
import basket.server.service.ValidationService;
import basket.server.util.HTMLUtil;
import basket.server.util.IllegalActionException;
import basket.server.util.types.storage.FileName;
import basket.server.util.types.storage.FileType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
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
    private final HTMLUtil htmlUtil;
    private final AppService appService;

    @PostMapping("upload/{token}")
    public ResponseEntity<Void> upload(HttpServletRequest request, HttpServletResponse response,
                                        @PathVariable String token) throws IOException, FileUploadException {

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
            storageService.upload(pendingUpload.getAppName(), inputStream, fileName, fileType);
        } catch (IllegalActionException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var optionalApp = appService.get(pendingUpload.getAppName());
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

    @ResponseBody
    @PostMapping(path = "upload/init", produces = TEXT_HTML_VALUE)
    @PreAuthorize("hasRole('DEVELOPER/' + #formPendingUpload.getAppName())")
    public String initUpload(HttpServletRequest request, HttpServletResponse response,
                             @ModelAttribute FormPendingUpload formPendingUpload) {

        var pendingUpload = validationService.validateFormPendingUpload(formPendingUpload);

        pendingUploadService.add(pendingUpload);

        return htmlUtil.getFileUploadFragment(request, response, pendingUpload.getType(), pendingUpload.getToken());
    }

    @GetMapping("download")
    public ResponseEntity<Resource> download(@RequestParam String appName, @RequestParam String fileName) throws IOException {
        Optional<InputStream> optionalStream;
        try {
            optionalStream = storageService.download(appName, fileName);
        } catch (IllegalActionException e) {
            return badRequest().build();
        }

        if (optionalStream.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .build();
        }

        InputStream inputStream = optionalStream.get();
        var resource = new InputStreamResource(inputStream);

        // var headers = new HttpHeaders();
        // headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=image.zip");

        return ResponseEntity.ok()
                // .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // TODO: secure access
    @PatchMapping("download/end")
    public ResponseEntity<Void> endDownload(@RequestParam String appName, @RequestParam String fileName) throws IOException {
        storageService.endDownload(appName, fileName);
        return ok().build();
    }
}
