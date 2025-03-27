package me.yeon.freship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FreshipApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreshipApplication.class, args);
    }

}
