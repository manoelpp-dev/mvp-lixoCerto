package com.lixocerto.mvpbackend.controllers;

import com.lixocerto.mvpbackend.model.InstrucaoDescarte;
import com.lixocerto.mvpbackend.services.InstrucaoDescarteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/intrucaoDescarte")

public class InstrucaoDescarteController {
    private final InstrucaoDescarteService service;

    public InstrucaoDescarteController(InstrucaoDescarteService service){
        this.service = service;
    }

    @GetMapping
    public List<InstrucaoDescarte> listar(){
        return service.listar();
    }

    @GetMapping("/{id}")
    public InstrucaoDescarte buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstrucaoDescarte criar(@RequestBody InstrucaoDescarte instrucao){
        return service.criar(instrucao);
    }

    @PutMapping("/{id}")
    public InstrucaoDescarte atualizar(@PathVariable Long id, @RequestBody InstrucaoDescarte instrucao){
        return service.atualizar(id, instrucao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        service.deletar(id);
    }
}
