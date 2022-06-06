package basket.server.model.input;

import basket.server.validation.annotations.Password;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SecureFormUser extends FormUser {

    @NotNull @Password
    private String currentPassword;
}
