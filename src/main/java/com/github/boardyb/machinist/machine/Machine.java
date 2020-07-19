package com.github.boardyb.machinist.machine;

import com.github.boardyb.restmodel.MachineTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "machine", schema = "machinist")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Machine {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "name")
    @NotNull
    @Size(min = 4, max = 100)
    private String name;

    @Column(name = "description")
    @Size(max = 1000)
    private String description;

    @Column(name = "year_of_production")
    @Min(1950)
    @Max(2020)
    private Integer yearOfProduction;

    @Column(name = "deleted")
    private boolean deleted;

    public Machine(String name, String description, Integer yearOfProduction) {
        this.name = name;
        this.description = description;
        this.yearOfProduction = yearOfProduction;
    }

    public MachineTO toDTO() {
        MachineTO machineTO = new MachineTO();
        machineTO.setId(id);
        machineTO.setCreatedAt(createdAt);
        machineTO.setUpdatedAt(updatedAt);
        machineTO.setName(name);
        machineTO.setDescription(description);
        machineTO.setYearOfProduction(yearOfProduction);
        return machineTO;
    }
}

