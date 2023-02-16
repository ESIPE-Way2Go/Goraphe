package fr.esipe.way2go.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String MESSAGE = "message";

    @ExceptionHandler(SimulationForbidden.class)
    public ResponseEntity<Object> handleSimulationForbidden(SimulationForbidden ex){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SimulationNotFoundException.class)
    public ResponseEntity<Object> handleSimulationNotFound(SimulationNotFoundException ex){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


}
