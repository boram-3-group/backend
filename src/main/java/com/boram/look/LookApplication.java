package com.boram.look;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LookApplication {

    public static void main(String[] args) {
        SpringApplication.run(LookApplication.class, args);
    }

}
