package basket.server.controller.api;

import basket.server.service.StorageService;
import basket.server.util.IllegalActionException;
import basket.server.util.storage.FileName;
import basket.server.util.storage.FileType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/storage")
public class StorageController {

    private final StorageService storageService;

    @PostMapping("create")
    public ResponseEntity<Void> create(@RequestParam String appName) throws IOException {
        try {
            storageService.create(appName);
        } catch (IllegalActionException e) {
            return badRequest().build();
        }

        return ok().build();
    }

    @PatchMapping("upload")
    public ResponseEntity<Void> upload(@RequestParam String appName, HttpServletRequest request) throws IOException {
        try {
            storageService.upload(appName, request.getInputStream(), FileName.STABLE, FileType.ZIP);
        } catch (IllegalActionException e) {
            return badRequest().build();
        }
        return ok().build();
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
