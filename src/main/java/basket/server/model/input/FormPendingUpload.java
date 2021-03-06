package basket.server.model.input;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FormPendingUpload {

    @NotBlank
    private String appId;

    @NotBlank
    private String type;

    @NotBlank
    private String version;
}
