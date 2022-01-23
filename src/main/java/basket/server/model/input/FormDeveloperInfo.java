package basket.server.model.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FormDeveloperInfo {

    @NotNull
    private FormPhoneNumber formPhoneNumber;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String phoneCode;
}
