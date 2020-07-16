package com.github.boardyb.machinist.machine;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "machine", schema = "machinist")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Machine {

    @Id
    @Column(name = "id")
    @Getter
    @Setter
    private String id;

    @Column(name = "created_at")
    @CreatedDate
    @Getter
    @Setter
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @CreatedDate
    @Getter
    @Setter
    private LocalDateTime updatedAt;

    @Column(name = "name")
    @Getter
    @Setter
    private String name;

    @Column(name = "deleted")
    @Getter
    @Setter
    private boolean deleted;
}

