package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.InfoService;

@RestController
public class InfoController {

    private final InfoService infoService;

    @Value("${server.port}")
    private Integer port;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/port")
    public String getPort(){
        return port.toString();
    }

    @GetMapping("/sum")
    public int sum(){
        return infoService.sum();
    }

}
