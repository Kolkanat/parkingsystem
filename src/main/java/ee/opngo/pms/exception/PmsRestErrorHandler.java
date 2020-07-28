package ee.opngo.pms.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.access.InvalidInvocationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.LocalDateTime;

@ControllerAdvice
public class PmsRestErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<Object> handleErrors(Throwable throwable, WebRequest request, ServletWebRequest servletWebRequest) {
        Throwable realError = throwable;
        if (throwable instanceof UndeclaredThrowableException) {
            realError = ((UndeclaredThrowableException) throwable).getUndeclaredThrowable();
        }

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (realError instanceof IOException || realError instanceof InvalidInvocationException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (realError instanceof EntityNotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (realError instanceof PmsException) {
            httpStatus = getHttpCodeByErrorCode(((PmsException) realError).getCode());
        }

        return new ResponseEntity<>(PmsRestError
                .builder()
                .status(httpStatus.value())
                .message(realError.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now()).build(), httpStatus);
    }

    private HttpStatus getHttpCodeByErrorCode(@NotNull PmsException.PmsExceptionCode code) {
        switch (code) {
            case DEFAULT:
                return HttpStatus.INTERNAL_SERVER_ERROR;
            case BAD_REQUEST:
                return HttpStatus.BAD_REQUEST;
            case VEHICLE_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case INCORRECT_SESSION_STATUS:
                return HttpStatus.PAYMENT_REQUIRED;
            case ALREADY_PARKED:
                return HttpStatus.BAD_REQUEST;
            case ASSET_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
