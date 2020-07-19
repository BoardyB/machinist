package com.github.boardyb.machinist.machine;

import com.github.boardyb.machinist.machine.exception.MachineDoesNotExistException;
import com.github.boardyb.restmodel.CreateMachineRequest;
import com.github.boardyb.restmodel.MachineTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MachineService {

    private MachineRepository machineRepository;

    @Autowired
    public MachineService(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public List<MachineTO> getAllMachines() {
        List<Machine> machines = this.machineRepository.findAllByDeletedFalseOrderByUpdatedAtDesc();
        log.debug("Fetched machines to return: [{}]", machines);
        return machines.stream().map((Machine::toDTO)).collect(Collectors.toList());
    }

    public MachineTO getMachineById(String id) {
        Machine machine = this.machineRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new MachineDoesNotExistException(id));
        return machine.toDTO();
    }

    public void deleteMachineById(String id) {
        Machine machine = this.machineRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new MachineDoesNotExistException(id));
        machine.setDeleted(true);
        log.debug("Deleting machine with id [{}]", id);
        this.machineRepository.save(machine);
    }

    public MachineTO createMachine(CreateMachineRequest createMachineRequest) {
        Machine machine = new Machine(createMachineRequest.getName(),
                createMachineRequest.getDescription(),
                createMachineRequest.getYearOfProduction()
        );
        Machine savedMachine = this.machineRepository.save(machine);
        log.debug("Machine saved with the following fields: [{}]", savedMachine);
        return savedMachine.toDTO();
    }

    public void updateMachine(MachineTO machineTO) {
        Machine machine = this.machineRepository.findByIdAndDeletedFalse(machineTO.getId())
                .orElseThrow(() -> new MachineDoesNotExistException(machineTO.getId()));
        machine.setName(machineTO.getName());
        machine.setDescription(machineTO.getDescription());
        machine.setYearOfProduction(machineTO.getYearOfProduction());
        this.machineRepository.save(machine);
        log.debug("Machine updated with the following fields: [{}]", machine);
    }

}
