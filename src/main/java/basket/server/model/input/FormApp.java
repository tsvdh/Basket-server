package basket.server.model.input;

import basket.server.validation.annotations.AppName;
import basket.server.validation.annotations.Description;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FormApp {

    @NotNull @AppName
    private String appName;

    @NotNull @Description
    private String description;
}
