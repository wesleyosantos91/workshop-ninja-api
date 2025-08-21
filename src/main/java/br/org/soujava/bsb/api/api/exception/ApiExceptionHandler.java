package br.org.soujava.bsb.api.api.exception;

import br.org.soujava.bsb.api.api.v1.response.CustomProblemDetail;
import br.org.soujava.bsb.api.api.v1.response.ErrorResponse;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.ServerHttpObservationFilter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private final MessageSource messageSource;

    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        final List<ErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse(fieldError.getField(), messageSource.getMessage(fieldError, LocaleContextHolder.getLocale())))
                .toList();

        final CustomProblemDetail problemDetail =
                new CustomProblemDetail(HttpStatus.BAD_REQUEST,"Validation failed", "The following errors occurred:", errors);
        final HttpServletRequest httpServletRequest = ((ServletWebRequest) request).getRequest();
        ServerHttpObservationFilter.findObservationContext(httpServletRequest).ifPresent(context -> context.setError(ex));

        LOGGER.error("Validation failed: {}", errors);

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<ProblemDetail> handleResourceNotFoundException(HttpServletRequest request, ResourceNotFoundException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());
        ServerHttpObservationFilter.findObservationContext(request).ifPresent(context -> context.setError(ex));;

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

}
