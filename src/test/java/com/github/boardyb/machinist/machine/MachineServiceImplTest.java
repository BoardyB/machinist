package com.github.boardyb.machinist.machine;

import com.github.boardyb.machinist.machine.exception.MachineDoesNotExistException;
import com.github.boardyb.restmodel.CreateMachineRequest;
import com.github.boardyb.restmodel.MachineTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MachineServiceImplTest {

    private MachineService machineService;
    private MachineRepository machineRepository;

    @BeforeEach
    void setUp() {
        this.machineRepository = Mockito.mock(MachineRepository.class);
        this.machineService = new MachineServiceImpl(this.machineRepository);
    }

    /**
     * - Given: a CreateMachineRequest with all fields filled correctly is being provided
     * - When: createMachine is being called with the forementioned object
     * - Then: a machine with the provided fields should be saved to the database and the method should
     * return with the DTO generated from the saved object
     */
    @Test
    void shouldCreateNewMachineIfValidPropertiesWereProvided() {
        CreateMachineRequest createMachineRequest = new CreateMachineRequest();
        createMachineRequest.setName("test machine");
        createMachineRequest.description("test description");
        createMachineRequest.setYearOfProduction(1999);

        Machine storedMachine = new Machine(createMachineRequest.getName(),
                createMachineRequest.getDescription(),
                createMachineRequest.getYearOfProduction()
        );
        storedMachine.setId("testId");
        doReturn(storedMachine).when(this.machineRepository).save(any(Machine.class));

        MachineTO machine = this.machineService.createMachine(createMachineRequest);

        assertThat(machine.getId(), equalTo(storedMachine.getId()));
        assertThat(machine.getName(), equalTo(createMachineRequest.getName()));
        assertThat(machine.getDescription(), equalTo(createMachineRequest.getDescription()));
        assertThat(machine.getYearOfProduction(), equalTo(createMachineRequest.getYearOfProduction()));
    }

    /**
     * - Given: a valid MachineTO is being provided and there is a machine with the same ID saved in the database.
     * - When: updateMachine is being called with the DTO.
     * - Then: the machine should be saved with the new fields.
     */
    @Test
    void shouldUpdateMachineIfPreviousMachineExistsInStore() {
        ArgumentCaptor<Machine> argumentCaptor = ArgumentCaptor.forClass(Machine.class);

        MachineTO machineTO = new MachineTO();
        machineTO.setId("testId");
        machineTO.setName("test machine with new name");
        machineTO.setDescription("test machine with new description");
        machineTO.setYearOfProduction(2010);

        Machine storedMachine = new Machine("test machine",
                "test description",
                1999
        );
        storedMachine.setId("testId");
        doReturn(Optional.of(storedMachine)).when(machineRepository).findByIdAndDeletedFalse(machineTO.getId());

        this.machineService.updateMachine(machineTO);

        verify(machineRepository, times(1)).save(argumentCaptor.capture());

        Machine updatedMachine = argumentCaptor.getValue();
        assertThat(updatedMachine.getId(), equalTo(machineTO.getId()));
        assertThat(updatedMachine.getName(), equalTo(machineTO.getName()));
        assertThat(updatedMachine.getDescription(), equalTo(machineTO.getDescription()));
        assertThat(updatedMachine.getYearOfProduction(), equalTo(machineTO.getYearOfProduction()));
    }

    /**
     * - Given: there is no machine saved with the provided id.
     * - When: updateMachine is being called with a DTO.
     * - Then: the machine update should fail and {@link MachineDoesNotExistException} should be thrown
     */
    @Test
    void shouldFailToUpdateMachineIfThereWasNoMachineStoredWithTheSameId() {
        MachineTO machineTO = new MachineTO();
        machineTO.setId("testId");

        doReturn(Optional.empty()).when(machineRepository).findByIdAndDeletedFalse(machineTO.getId());

        assertThrows(MachineDoesNotExistException.class, () -> this.machineService.updateMachine(machineTO));
    }

    /**
     * - Given: there is a machine stored with an ID which matches the provided one.
     * - When: getMachineById is being called with a valid machine ID.
     * - Then: the machine should be fetched from the database and returned as DTO.
     */
    @Test
    void shouldGetMachineIfThereIsOneStoredWithTheSameId() {
        String testId = "testId";

        Machine storedMachine = new Machine("test machine",
                "test description",
                1999
        );
        storedMachine.setId(testId);
        doReturn(Optional.of(storedMachine)).when(machineRepository).findByIdAndDeletedFalse(testId);

        MachineTO machine = this.machineService.getMachineById(testId);
        assertThat(machine.getId(), equalTo(storedMachine.getId()));
        assertThat(machine.getName(), equalTo(storedMachine.getName()));
        assertThat(machine.getDescription(), equalTo(storedMachine.getDescription()));
        assertThat(machine.getYearOfProduction(), equalTo(storedMachine.getYearOfProduction()));
    }

    /**
     * - Given: there is no machine saved with the provided id.
     * - When: getMachineById is being called with a valid machine ID.
     * - Then: the fetch of the machine should fail and {@link MachineDoesNotExistException} should be thrown.
     */
    @Test
    void shouldFailToGetMachineIfThereWasNoMachineStoredWithTheSameId() {
        String testId = "testId";

        doReturn(Optional.empty()).when(machineRepository).findByIdAndDeletedFalse(testId);

        assertThrows(MachineDoesNotExistException.class, () -> this.machineService.getMachineById(testId));
    }

    /**
     * - Given: there are machines saved in the database
     * - When: getAllMachines is being called to return all machines in the database
     * - Then: all stored machines should be fetched and should be returned as DTOs
     */
    @Test
    void shouldFetchAllStoredMachines() {
        Machine storedMachine = new Machine("test machine",
                "test description",
                1999
        );
        storedMachine.setId("testId");
        Machine storedMachine1 = new Machine("test machine",
                "test description",
                1999
        );
        storedMachine1.setId("testId1");
        List<Machine> storedMachines = newArrayList(storedMachine, storedMachine1);

        doReturn(storedMachines).when(machineRepository).findAllByDeletedFalseOrderByUpdatedAtDesc();

        List<MachineTO> machines = this.machineService.getAllMachines();
        MachineTO machineTO = machines.get(0);

        assertThat(machineTO.getId(), equalTo(storedMachine.getId()));
        assertThat(machineTO.getName(), equalTo(storedMachine.getName()));
        assertThat(machineTO.getDescription(), equalTo(storedMachine.getDescription()));
        assertThat(machineTO.getYearOfProduction(), equalTo(storedMachine.getYearOfProduction()));

        MachineTO machineTO1 = machines.get(1);
        assertThat(machineTO1.getId(), equalTo(storedMachine1.getId()));
        assertThat(machineTO1.getName(), equalTo(storedMachine1.getName()));
        assertThat(machineTO1.getDescription(), equalTo(storedMachine1.getDescription()));
        assertThat(machineTO1.getYearOfProduction(), equalTo(storedMachine1.getYearOfProduction()));
    }

    /**
     * - Given: there is a machine saved in the database.
     * - When: deleteMachineById is being called with a valid id to delete the stored machine.
     * - Then: the machine's deleted field should be set to true and saved to the database.
     */
    @Test
    void shouldDeleteStoredMachine() {
        ArgumentCaptor<Machine> argumentCaptor = ArgumentCaptor.forClass(Machine.class);

        String machineId = "testId";
        Machine storedMachine = new Machine("test machine",
                "test description",
                1999
        );
        storedMachine.setId(machineId);

        doReturn(Optional.of(storedMachine)).when(machineRepository).findByIdAndDeletedFalse(machineId);

        this.machineService.deleteMachineById(machineId);

        verify(machineRepository, times(1)).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().isDeleted(), equalTo(true));
    }

    /**
     * - Given: there are no machines saved in the database.
     * - When: deleteMachineById is being called with a valid id to delete a stored machine.
     * - Then: the machine deletion should fail and {@link MachineDoesNotExistException} should be thrown.
     */
    @Test
    void shouldFailToDeleteMachineIfThereWasNoMachineStoredWithTheSameId() {
        String machineId = "testId";

        doReturn(Optional.empty()).when(machineRepository).findByIdAndDeletedFalse(machineId);

        assertThrows(MachineDoesNotExistException.class, () -> this.machineService.deleteMachineById(machineId));
    }
}