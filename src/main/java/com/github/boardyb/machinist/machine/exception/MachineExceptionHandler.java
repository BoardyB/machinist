package com.github.boardyb.machinist.machine.exception;

import com.github.boardyb.restmodel.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class MachineExceptionHandler {

    @ExceptionHandler(MachineDoesNotExistException.class)
    public ResponseEntity<?> handleMachineNotFoundException(MachineDoesNotExistException e) {
        log.error("\n" + ExceptionUtils.getStackTrace(e));
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorResponse,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArgumentException(MethodArgumentNotValidException e) {
        log.error("\n" + ExceptionUtils.getStackTrace(e));
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
