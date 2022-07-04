package basket.server.controller;

import basket.server.model.input.FormApp;
import basket.server.model.input.FormPendingUpload;
import basket.server.model.input.FormUser;
import basket.server.model.user.User;
import basket.server.service.AppService;
import basket.server.service.StorageService;
import basket.server.service.UserService;
import basket.server.service.VerificationCodeService;
import basket.server.util.ControllerUtil;
import basket.server.util.HTMLUtil;
import basket.server.util.IllegalActionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;
    private final AppService appService;
    private final StorageService storageService;
    private final VerificationCodeService verificationCodeService;
    private final ControllerUtil controllerUtil;

    @GetMapping("login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("register")
    public String getRegistrationPage(Model model) {
        model.addAttribute("formUser", new FormUser());
        model.addAttribute("countryCodeList", HTMLUtil.getCountryList());
        return "register";
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@ModelAttribute FormUser formUser,
                                         HttpServletRequest request) throws ServletException {
        try {
            userService.add(formUser);
        } catch (IllegalActionException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        request.logout();
        request.login(formUser.getUsername(), formUser.getPassword());

        return ok().build();
    }

    @GetMapping("create")
    public String getNewPage(Model model) {
        model.addAttribute("formApp", new FormApp());
        return "create";
    }

    @PostMapping("apps/create")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<Void> addApp(@ModelAttribute FormApp formApp, Authentication auth,
                                       HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            appService.add(formApp, request.getRemoteUser());
        } catch (IllegalActionException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        List<GrantedAuthority> authorities = new ArrayList<>(auth.getAuthorities());
        authorities.add(new SimpleGrantedAuthority("ROLE_DEVELOPER/" + formApp.getAppName()));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN/" + formApp.getAppName()));

        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        try {
            storageService.create(formApp.getAppName());
        } catch (IllegalActionException e) {
            // ignore as pageApp name cannot be taken at this point and no exception should be thrown
        }

        response.sendRedirect("/apps/%s/releases".formatted(formApp.getAppName()));

        return ok().build();
    }

    // @PutMapping
    // @PreAuthorize("hasRole('DEVELOPER-' + updatedApp.getName())")
    // public ResponseEntity<Void> updateApp(@RequestBody @NonNull @Validated App updatedApp) {
    //     appService.update(updatedApp);
    //
    //     return ok().build();
    // }

    @GetMapping("apps/{appName}")
    public ResponseEntity<Void> getAppPage(@PathVariable String appName,
                                           HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getRequestURI() + "/overview");

        return ok().build();
    }

    @GetMapping("apps/{appName}/overview")
    public ModelAndView getAppOverviewPage(@PathVariable String appName) {
        var modelAndView = new ModelAndView("app/overview");

        modelAndView.addObject("pageApp", controllerUtil.getApp(appName));

        return modelAndView;
    }

    @PreAuthorize("hasRole('DEVELOPER/' + #appName)")
    @GetMapping("apps/{appName}/manage")
    public ModelAndView getAppManagePage(@PathVariable String appName) {
        var modelAndView = new ModelAndView("app/manage");

        modelAndView.addObject("pageApp", controllerUtil.getApp(appName));

        return modelAndView;
    }

    @PreAuthorize("hasRole('DEVELOPER/' + #appName)")
    @GetMapping("apps/{appName}/releases")
    public ModelAndView getAppReleasesPage(@PathVariable String appName) {
        var modelAndView = new ModelAndView("app/releases");

        modelAndView.addObject("pageApp", controllerUtil.getApp(appName));
        modelAndView.addObject("formPendingUpload", new FormPendingUpload());
        modelAndView.addObject("storageService", storageService);

        return modelAndView;
    }


    @GetMapping("users/{pageUsername}")
    public ResponseEntity<Void> getUserPage(@PathVariable String pageUsername,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.sendRedirect(request.getRequestURI() + "/home");

        return ok().build();
    }

    @GetMapping("users/{pageUsername}/home")
    public ModelAndView getUserHomePage(@PathVariable String pageUsername) {
        var modelAndView = new ModelAndView("user/home");

        modelAndView.addObject("pageUser", controllerUtil.getUser(pageUsername));

        return modelAndView;
    }

    @GetMapping("users/{pageUsername}/projects")
    public ModelAndView getUserProjectsPage(@PathVariable String pageUsername) {
        var modelAndView = new ModelAndView("user/projects");

        User user = controllerUtil.getUser(pageUsername);
        if (!user.isDeveloper()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    "User %s is not a developer".formatted(user.getUsername()));
        }

        modelAndView.addObject("pageUser", user);

        return modelAndView;
    }

    @PreAuthorize("authentication.name.equals(#pageUsername)")
    @GetMapping("users/{pageUsername}/profile")
    public ModelAndView getUserProfilePage(@PathVariable String pageUsername) {
        var modelAndView = new ModelAndView("user/profile");

        modelAndView.addObject("pageUser", controllerUtil.getUser(pageUsername));

        return modelAndView;
    }

    @PreAuthorize("authentication.name.equals(#pageUsername)")
    @GetMapping("users/{pageUsername}/settings")
    public ModelAndView getUserSettingsPage(@PathVariable String pageUsername) {
        var modelAndView = new ModelAndView("user/settings");

        modelAndView.addObject("pageUser", controllerUtil.getUser(pageUsername));

        return modelAndView;
    }
}
