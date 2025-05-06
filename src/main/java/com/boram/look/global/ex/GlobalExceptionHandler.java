package com.boram.look.global.ex;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAndUsernameNotEqualException.class)
    public ResponseEntity<?> handleEmailAndUsernameNotEqualException(EmailAndUsernameNotEqualException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DuplicateEmailUseException.class)
    public ResponseEntity<?> handleDuplicateEmailUseException(DuplicateEmailUseException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<?> handleEmailNotVerifiedException(EmailNotVerifiedException e) {
        String message = "email not verified.\nemail: " + e.getEmail();
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<?> handleJsonMappingException(JsonMappingException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NoExistRegistrationException.class)
    public ResponseEntity<?> handleNoExistRegistrationException(NoExistRegistrationException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof SQLException sqlEx) {
            if (sqlEx.getSQLState().startsWith("23")) {
                // SQLState 23xxx는 constraint violation을 나타냄
                log.error("데이터 중복 오류가 발생했습니다: " + sqlEx.getMessage());
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("데이터 중복 오류가 발생했습니다.");
            }
        }

        // 다른 DataIntegrityViolationException 경우
        log.error("데이터 중복 오류가 발생했습니다: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부 오류가 발생했습니다. ");
    }

}
