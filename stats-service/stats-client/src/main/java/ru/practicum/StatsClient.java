package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatsClient {
    public static void main(String[] args) {
        SpringApplication.run(StatsClient.class, args);
//        List<String> list = new ArrayList<>();
//        list.add("1wew");
//        list.add("2edrfg");
//        var fg = "2024-01-17 20:07:40";
//        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime timeCheck = LocalDateTime.parse(fg, formatter);
//        String sad = timeCheck.format(formatter);
//
//        System.out.println(LocalDateTime.now().toString());
    }
}