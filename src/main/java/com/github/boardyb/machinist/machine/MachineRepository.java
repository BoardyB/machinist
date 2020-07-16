package com.github.boardyb.machinist.machine;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MachineRepository extends CrudRepository<Machine, String> {

    List<Machine> findAllByDeletedFalseOrderByUpdatedAtDesc();

    Optional<Machine> findByIdAndDeletedFalse(String id);
}
