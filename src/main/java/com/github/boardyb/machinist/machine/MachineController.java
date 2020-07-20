package com.github.boardyb.machinist.machine;

import com.github.boardyb.restapi.MachineApi;
import com.github.boardyb.restmodel.CreateMachineRequest;
import com.github.boardyb.restmodel.MachineTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController()
public class MachineController implements MachineApi {

    @Autowired
    private MachineService machineService;

    @Override
    public ResponseEntity<Void> createMachine(@Valid CreateMachineRequest body) {
        MachineTO machine = this.machineService.createMachine(body);
        return ResponseEntity.created(URI.create("/api/machine/" + machine.getId())).build();
    }

    @Override
    public ResponseEntity<Void> deleteMachine(String machineId) {
        this.machineService.deleteMachineById(machineId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<MachineTO>> getAllMachines() {
        return ResponseEntity.ok(this.machineService.getAllMachines());
    }

    @Override
    public ResponseEntity<MachineTO> getMachineById(String machineId) {
        return ResponseEntity.ok(this.machineService.getMachineById(machineId));
    }

    @Override
    public ResponseEntity<Void> updateMachine(@Valid MachineTO body) {
        this.machineService.updateMachine(body);
        return ResponseEntity.ok().location(URI.create("/api/machine/" + body.getId())).build();
    }
}
