package com.tribia.application.exception.handler;

import com.tribia.application.dto.ApiResponse;
import com.tribia.application.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(ApiResponse
                .failure(String.join(", ", errors)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ApiResponse
                .failure(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        return new ResponseEntity<>(ApiResponse
                .failure(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VehicleAlreadySubscribedException.class)
    public ResponseEntity<ApiResponse<Void>> handleVehicleAlreadySubscribedException(VehicleAlreadySubscribedException ex) {
        return new ResponseEntity<>(ApiResponse.failure(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VehicleNotAlreadySubscribedException.class)
    public ResponseEntity<ApiResponse<Void>> handleVehicleNotAlreadySubscribedException(VehicleNotAlreadySubscribedException ex) {
        return new ResponseEntity<>(ApiResponse.failure(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadySubscribedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadySubscribedException(AlreadySubscribedException ex) {
        return new ResponseEntity<>(ApiResponse.failure(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyUnsubscribedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadyUnsubscribedException(AlreadyUnsubscribedException ex) {
        return new ResponseEntity<>(ApiResponse
                .failure(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SVVBadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleSVVBadRequestException(SVVBadRequestException ex) {
        return new ResponseEntity<>(ApiResponse
                .failure(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SVVForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleSVVForbiddenException(SVVForbiddenException ex) {
        return new ResponseEntity<>(ApiResponse
                .failure(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SVVRequestQuotaExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleSVVRequestQuotaExceededException(SVVRequestQuotaExceededException ex) {
        return new ResponseEntity<>(ApiResponse
                .failure(ex.getMessage()), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(SVVInformationNotAvailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleSVVInformationNotAvailableException(SVVInformationNotAvailableException ex) {
        return new ResponseEntity<>(ApiResponse
                .failure(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return new ResponseEntity<>(ApiResponse
                .failure(String.format("Error while processing the request : %s",
                        ex.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
