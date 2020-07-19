package com.github.boardyb.machinist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing()
public class MachinistApplication {

    public static void main(String[] args) {
        SpringApplication.run(MachinistApplication.class, args);
    }

}
