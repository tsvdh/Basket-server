package basket.server.util;

import basket.server.model.app.App;
import basket.server.model.input.FormApp;
import basket.server.model.input.FormPendingUpload;
import basket.server.model.input.FormUser;
import basket.server.model.user.User;
import basket.server.service.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IntelliSenseHelper {

    @GetMapping("1")
    private String method1(Model model) {
        model.addAttribute("formApp", new FormApp());
        return "fragments/inputs/app";
    }

    @GetMapping("2")
    private String method2(Model model) {
        model.addAttribute("formUser", new FormUser());
        model.addAttribute("countryCodeList", HTMLUtil.getCountryList());
        return "fragments/inputs/user";
    }

    @GetMapping("3")
    private String method3(Model model) {
        model.addAttribute("pageUser", new User());
        return "fragments/page-components";
    }

    @GetMapping("4")
    private String method4(Model model) {
        model.addAttribute("app", new App());
        model.addAttribute("formPendingUpload", new FormPendingUpload());
        model.addAttribute("storageService", new StorageService(null));

        return "fragments/elements/app/releases";
    }
}
