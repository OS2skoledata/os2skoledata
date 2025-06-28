package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.api.model.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handleRuntimeException(final RuntimeException ex, final HttpServletRequest request) {
        log.error("Unknown exception during api call", ex);
        return ErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseBody
    public ResponseEntity<ErrorDTO> handleInvalidRequestException(final ResponseStatusException ex, final HttpServletRequest request) {
        // Hvis de hellere vil have text/plain, man man også blot returnere en streng herfra.
        return new ResponseEntity<>(ErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getRawStatusCode())
                .error(ex.getReason())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .build(),
                ex.getStatus());
    }

}
