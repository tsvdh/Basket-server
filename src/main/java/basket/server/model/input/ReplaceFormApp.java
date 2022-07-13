package basket.server.model.input;

import basket.server.validation.annotations.AppName;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReplaceFormApp extends FormApp {

    @NotNull @AppName
    private String id;
}
