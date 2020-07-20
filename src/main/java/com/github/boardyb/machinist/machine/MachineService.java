package com.github.boardyb.machinist.machine;

import com.github.boardyb.machinist.machine.exception.MachineDoesNotExistException;
import com.github.boardyb.restmodel.CreateMachineRequest;
import com.github.boardyb.restmodel.MachineTO;

import java.util.List;

/**
 * Defines operations which are intended to manage machines in the application.
 * These operations consist of: creation, fetch, update, deletion of machines in the database.
 */
public interface MachineService {

    /**
     * Fetches all machine records from database.
     *
     * @return list of DTOs of machines retrieved from the database
     * or empty list if none was found.
     */
    List<MachineTO> getAllMachines();

    /**
     * Fetches a single machine from the database.
     *
     * @param id the id field of the machine which will be fetched.
     * @return DTO of the machine fetched from the database.
     * @throws MachineDoesNotExistException if cannot find any machines in the database with the provided id.
     */
    MachineTO getMachineById(String id) throws MachineDoesNotExistException;

    /**
     * Deletes a single machine from the database.
     *
     * @param id the id field of the machine which will be deleted.
     * @throws MachineDoesNotExistException if cannot find any machines in the database with the provided id.
     */
    void deleteMachineById(String id) throws MachineDoesNotExistException;

    /**
     * Creates a new machine with the provided fields.
     *
     * @param createMachineRequest provides information about the machine which will be created
     *                             eg.: name (required), description (optional), yearOfProduction (optional)
     * @return DTO of the machine which was created.
     */
    MachineTO createMachine(CreateMachineRequest createMachineRequest);

    /**
     * Updates a machine in the database with the provided fields.
     *
     * @param machineTO DTO of the machine which contains the fields
     *                  which will be updated on the machine in the database.
     * @throws MachineDoesNotExistException if cannot find any machines in the database with the provided id.
     */
    void updateMachine(MachineTO machineTO) throws MachineDoesNotExistException;
}
