package basket.server.util;

import basket.server.model.input.FormUser;
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

@Component
@RequiredArgsConstructor
public class HTMLUtil {

    private final ServletContext servletContext;
    private final SpringTemplateEngine templateEngine;

    public String process(HttpServletRequest request, HttpServletResponse response,
                          String viewName, Map<String, Object> model) {

        var requestContext = new SpringWebMvcThymeleafRequestContext(
                new RequestContext(request, response, servletContext, model),
                request
        );

        var templateContext = new Context();
        templateContext.setVariable(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, requestContext);

        return templateEngine.process(viewName, templateContext);
    }

    @SuppressWarnings("ConstantConditions")
    public String getInputFragment(String html, String fragmentAttribute,
                                   String inputValue, @Nullable List<String> faults) {
        var document = Jsoup.parse(html);

        Element fragment = document.getElementById(fragmentAttribute + "Fragment");

        Element inputElement = fragment.getElementById(fragmentAttribute + "Input");
        inputElement.val(inputValue);

        if (inputValue.equals("")) {
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

    public String getRegisterInputFragment(HttpServletRequest request, HttpServletResponse response,
                                           String fragmentAttribute, String inputValue,
                                           @Nullable List<String> faults) {
        var map = new HashMap<String, Object>();
        map.put("formUser", new FormUser());
        String html = process(request, response, "fragments/input", map);

        return getInputFragment(html,
                fragmentAttribute,
                inputValue,
                faults);
    }
}
