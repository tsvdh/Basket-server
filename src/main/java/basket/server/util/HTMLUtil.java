package basket.server.util;

import basket.server.model.input.FormApp;
import basket.server.model.input.FormPendingUpload;
import basket.server.model.input.FormUser;
import com.neovisionaries.i18n.CountryCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.context.webmvc.SpringWebMvcThymeleafRequestContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;

@Component
@RequiredArgsConstructor
public class HTMLUtil {

    private final ServletContext servletContext;
    private final SpringTemplateEngine templateEngine;

    public static List<CountryCode> getCountryList() {
        return stream(CountryCode.values())
                .sorted(comparing(CountryCode::getName))
                .toList();
    }

    private String thymeleafProcess(HttpServletRequest request, HttpServletResponse response,
                                    String viewName, Map<String, Object> model) {
        var requestContext = new SpringWebMvcThymeleafRequestContext(
                new RequestContext(request, response, servletContext, model),
                request
        );

        // new WebContext(request, response, new ApplicationContext(new StandardContext()));
        var templateContext = new Context();
        templateContext.setVariable(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, requestContext);

        return templateEngine.process(viewName, templateContext);
    }

    @SuppressWarnings("ConstantConditions")
    private String getInputFragment(String html, String fragmentAttribute,
                                   String inputValue, @Nullable List<String> faults) {
        var document = Jsoup.parse(html);

        Element fragment = document.getElementById(fragmentAttribute + "Fragment");

        if (inputValue.equals("")) {
            return fragment.outerHtml();
        }

        Element inputElement = fragment.getElementById(fragmentAttribute + "Input");
        inputElement.val(inputValue);

        if (faults.isEmpty()) {
            inputElement.addClass("is-valid");
        } else {
            inputElement.addClass("is-invalid");
            inputElement.parent().addClass("has-validation");
        }

        for (String fault : faults) {
            var faultElement = new Element("div")
                    .text(fault)
                    .addClass("invalid-feedback");

            inputElement.parent().appendChild(faultElement);
        }

        return fragment.outerHtml();
    }

    public enum InputType {
        USER,
        APP
    }

    private static final Map<String, Object> USER_INPUT_MODEL = getUserInputModel();
    private static final Map<String, Object> APP_INPUT_MODEL = getAppInputModel();
    private static final Map<String, Object> ELEMENTS_MODEL = getElementsModel();

    private static Map<String, Object> getUserInputModel() {
        var model = new HashMap<String, Object>();
        model.put("formUser", new FormUser());
        model.put("countryCodeList", HTMLUtil.getCountryList());
        return model;
    }

    private static Map<String, Object> getAppInputModel() {
        var model = new HashMap<String, Object>();
        model.put("formApp", new FormApp());
        return model;
    }

    private static Map<String, Object> getElementsModel() {
        var model = new HashMap<String, Object>();
        model.put("formPendingUpload", new FormPendingUpload());
        return model;
    }

    public String getInputFragment(HttpServletRequest request, HttpServletResponse response,
                                    String fragmentAttribute, String inputValue,
                                    @Nullable List<String> faults, InputType inputType) {

        String templateName = "fragments/inputs/" + inputType.toString().toLowerCase();
        var model = switch (inputType) {
            case APP -> APP_INPUT_MODEL;
            case USER -> USER_INPUT_MODEL;
        };

        String html = thymeleafProcess(request, response, templateName, model);

        return getInputFragment(html, fragmentAttribute, inputValue, faults);
    }

    @SuppressWarnings("ConstantConditions")
    public String getFileUploadFragment(HttpServletRequest request, HttpServletResponse response, String type, String token) {
        String html = thymeleafProcess(request, response, "fragments/elements/app/releases", ELEMENTS_MODEL);

        var document = Jsoup.parse(html);

        var fragment = document.getElementById(type + "UploadFragment");

        var form = fragment.getElementsByTag("form").get(0);
        form.attr("hx-post", form.attr("hx-post") + token);

        return fragment.outerHtml();
    }
}
