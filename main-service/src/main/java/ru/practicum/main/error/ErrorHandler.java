package ru.practicum.main.error;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.xml.bind.ValidationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ApiError buildApiError(Throwable exception, HttpStatus status, String reason) {
        return ApiError.builder()
                .errors(getStackTraceAsString(exception))
                .message(exception.getMessage())
                .reason(reason)
                .status(status.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return buildApiError(exception, HttpStatus.BAD_REQUEST, "Incorrect request.");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(ValidationException exception) {
        return buildApiError(exception, HttpStatus.BAD_REQUEST, "Incorrect request.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleArgumentValidationException(MethodArgumentNotValidException exception) {
        return ApiError.builder()
                .errors(getStackTraceAsString(exception))
                .message(getValidationErrorMsg(exception))
                .reason("Incorrect request.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(EntityNotFoundException exception) {
        return buildApiError(exception, HttpStatus.NOT_FOUND, "The required object was not found.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingRequestParamException(MissingServletRequestParameterException exception) {
        return buildApiError(exception, HttpStatus.BAD_REQUEST, "Incorrect request.");
    }

    @ExceptionHandler({EditingErrorException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictExceptions(RuntimeException exception) {
        return buildApiError(exception, HttpStatus.CONFLICT, "For the requested operation the conditions are not met.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGenericException(Throwable exception) {
        return buildApiError(exception, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.");
    }

    private String getStackTraceAsString(Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }

    private String getValidationErrorMsg(MethodArgumentNotValidException exception) {
        return String.format("Field: %s. Error: %s", Objects.requireNonNull(exception.getFieldError()).getField(),
                exception.getFieldError().getDefaultMessage());
    }
}
