package basket.server.handlers;

import basket.server.util.BadRequestException;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    @ExceptionHandler(BadRequestException.class)
    public ModelAndView handleException(BadRequestException e) {
        log.warn("Bad request", e);

        ModelAndView errorView = new ModelAndView("error");
        errorView.setStatus(HttpStatus.BAD_REQUEST);
        return errorView;
    }
}
