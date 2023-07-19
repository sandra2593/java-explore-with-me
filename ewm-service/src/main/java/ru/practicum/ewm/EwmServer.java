package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum.stats.gateway", "ru.practicum.ewm"})
public class EwmServer {

    public static void main(String[] args) {
        SpringApplication.run(EwmServer.class, args);
    }

}
