package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.practicum.config.Config;

@SpringBootApplication
@Import(Config.class)
public class MainService {
    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
    }
}