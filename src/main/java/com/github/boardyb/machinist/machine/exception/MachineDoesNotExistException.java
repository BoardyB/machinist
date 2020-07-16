package com.github.boardyb.machinist.machine.exception;

public class MachineDoesNotExistException extends RuntimeException {

    public MachineDoesNotExistException(String id) {
        super("Message with id [" + id + "] does not exist!");
    }
}
