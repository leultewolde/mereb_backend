package app.mereb.mereb_backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String message;
    private String debugMessage;
}
