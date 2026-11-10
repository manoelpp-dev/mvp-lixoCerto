package com.lixocerto.mvpbackend.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categoriaLixo")

public class CategoriaLixoController {

    @GetMapping
    public String hello() {
        return "Hello!";
    }
}
