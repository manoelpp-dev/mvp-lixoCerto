package com.lixocerto.mvpbackend.controllers;


import com.lixocerto.mvpbackend.model.PontoDescarte;
import com.lixocerto.mvpbackend.services.PontoDescarteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pontoDescarte")
public class PontoDescarteController {
    private final PontoDescarteService service;

    public PontoDescarteController(PontoDescarteService service) {
        this.service = service;
    }

    @GetMapping
    public List<PontoDescarte> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public PontoDescarte buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PontoDescarte criar(@RequestBody PontoDescarte ponto){
        return service.criar(ponto);
    }

    @PutMapping("/{id}")
    public PontoDescarte atualizar(@PathVariable Long id, @RequestBody PontoDescarte ponto){
        return service.atualizar(id, ponto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        service.deletar(id);
    }
}