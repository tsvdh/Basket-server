package basket.server.model.input;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FormPhoneNumber {

    @NotBlank
    private String regionCode;

    @NotBlank
    private String number;
}
