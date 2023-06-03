package nlu.dacn.dacn_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public ServiceException(String message) {
        super(message);
        this.message = message;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ServiceException(HttpStatus status, String message) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
