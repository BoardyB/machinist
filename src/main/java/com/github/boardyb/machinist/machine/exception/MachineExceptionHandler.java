package com.github.boardyb.machinist.machine.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MachineExceptionHandler {

    @ExceptionHandler(MachineDoesNotExistException.class)
    public ResponseEntity<?> handleMachineNotFoundException(MachineDoesNotExistException e) {
        log.error("\n" + ExceptionUtils.getStackTrace(e));
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArgumentException(MethodArgumentNotValidException e) {
        log.error("\n" + ExceptionUtils.getStackTrace(e));
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}
