package basket.server.handlers;

import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ModelAndView handleException(ValidationException e) {
        log.warn("Invalid request", e);

        ModelAndView errorView = new ModelAndView("error");
        errorView.setStatus(HttpStatus.BAD_REQUEST);
        return errorView;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ModelAndView handleException(HttpClientErrorException e) {
        ModelAndView errorView = new ModelAndView("error");
        errorView.setStatus(e.getStatusCode());
        errorView.addObject("details", e.getStatusText());
        return errorView;
    }
}
