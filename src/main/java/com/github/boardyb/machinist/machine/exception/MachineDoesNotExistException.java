package com.github.boardyb.machinist.machine.exception;

public class MachineDoesNotExistException extends RuntimeException {

    public MachineDoesNotExistException(String id) {
        super("Machine with id [" + id + "] does not exist!");
    }
}
