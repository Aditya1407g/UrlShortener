package com.aditya.urlshortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFound(UrlNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(
                404,
                "Not Found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse>  handleNoResourceFound(NoResourceFoundException ex){
        ErrorResponse err = new ErrorResponse(
                404,
                "Not Found",
                "Resource not found: /" + ex.getResourcePath()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ValidationErrorResponse> handleUsernameExists(UsernameAlreadyExistsException ex) {
        List<FieldValidationError> details = List.of(
                new FieldValidationError("username", "Username already taken")
        );
        ValidationErrorResponse body = new ValidationErrorResponse(
                400,
                "Bad Request",
                "Validation failed",
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        ErrorResponse body = new ErrorResponse(
                401,
                "Unauthorized",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldValidationError> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldValidationError(err.getField(), err.getDefaultMessage()))
                .toList();

        ValidationErrorResponse body = new ValidationErrorResponse(
                400,
                "Bad Request",
                "Validation failed",
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex){
        log.error("Unhandled exception", ex);

        ErrorResponse body = new ErrorResponse(
                500,
                "Internal Server Error",
                "Something went wrong"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }


}
