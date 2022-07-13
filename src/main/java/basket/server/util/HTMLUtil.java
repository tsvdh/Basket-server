package basket.server.util;

import basket.server.model.input.FormApp;
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

        var templateContext = new Context();
        templateContext.setVariable(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, requestContext);
        templateContext.setVariables(model);

        return templateEngine.process(viewName, templateContext);
    }

    @SuppressWarnings("ConstantConditions")
    private String getInputFragment(String html, String fragmentAttribute,
                                   String inputValue, @Nullable List<String> faults) {
        var document = Jsoup.parse(html);

        Element fragment = document.getElementById(fragmentAttribute + "Fragment");

        Element inputElement = fragment.getElementById(fragmentAttribute + "Input");
        inputElement.val(inputValue);

        if (faults == null) {
            return fragment.outerHtml();
        }

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

    public String getInputFragment(HttpServletRequest request, HttpServletResponse response,
                                   String fragmentAttribute, String inputValue,
                                   @Nullable List<String> faults, InputType inputType) {
        return getInputFragment(request, response, fragmentAttribute, inputValue, faults, inputType, null, null);
    }

    public String getInputFragment(HttpServletRequest request, HttpServletResponse response,
                                   String fragmentAttribute, String inputValue,
                                   @Nullable List<String> faults, InputType inputType,
                                   String extraKey, Object extraValue) {

        String templateName = "fragments/inputs/" + inputType.toString().toLowerCase();

        var model = switch (inputType) {
            case APP -> {
                var appModel = new HashMap<String, Object>();
                appModel.put("formApp", new FormApp());
                yield appModel;
            }
            case USER -> {
                var userModel = new HashMap<String, Object>();
                userModel.put("formUser", new FormUser());
                userModel.put("countryCodeList", HTMLUtil.getCountryList());
                yield userModel;
            }
        };

        if (extraKey != null && extraValue != null) {
            model.put(extraKey, extraValue);
        }

        String html = thymeleafProcess(request, response, templateName, model);

        return getInputFragment(html, fragmentAttribute, inputValue, faults);
    }
}
