package com.lixocerto.mvpbackend.controllers;


import com.lixocerto.mvpbackend.model.CategoriaLixo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoriaLixo")

public class CategoriaLixoController {

    private final CategoriaLixoService service;

    public CategoriaLixoController(CategoriaLixoService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoriaLixo> listar(){
        return service.listar();
    }

    @GetMapping("/{id}")
    public CategoriaLixo buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaLixo criar(@RequestBody CategoriaLixo categoria){
        return service.criar(categoria);
    }

    @PutMapping("/{id}")
    public CategoriaLixo atualizar(@PathVariable Long id, @RequestBody CategoriaLixo categoria){
        return service.atualizar(id, categoria);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleter(@PathVariable Long id){
        service.deletar(id);
    }
}
