package ecommerce.weariva.weariva_ecommerce.exceptions;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // If We getting any kind of IOException in Every Server Side Render then this Controller Advices handle the code.
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        String message = ex.getMessage();

        if (message != null && (
                message.contains("An established connection was aborted") ||
                message.contains("Broken pipe") ||
                message.contains("Connection reset"))) {
            
            // Just log quietly and do nothing
            System.out.println("CientEvent Source disconnected - IO exception.");
            return ResponseEntity.status(HttpStatus.OK).build(); // no error response
        }

        // Log other IOExceptions normally
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected IO error occurred.");
    }
}
