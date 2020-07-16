package com.github.boardyb.machinist.machine;

import com.github.boardyb.machinist.machine.exception.MachineDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class MachineService {

    @Autowired
    private MachineRepository machineRepository;

    public List<Machine> getAllMachines() {
        return newArrayList(this.machineRepository.findAllByDeletedFalseOrderByUpdatedAtDesc());
    }

    public Machine getMachineById(String id) {
        return this.machineRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new MachineDoesNotExistException(id));
    }

    public void deleteMachineById(String id) {
        Machine machine = this.machineRepository.findById(id).orElseThrow(() -> new MachineDoesNotExistException(id));
        machine.setDeleted(true);
        this.machineRepository.save(machine);
    }


}
