package com.lixocerto.mvpbackend.controllers;


import com.lixocerto.mvpbackend.dto.LixoRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lixo")
public class LixoController {
    private final LixoService service;

    public LixoController(LixoService service) {
        this.service = service;
    }

    @GetMapping
    public List<LixoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public LixoResponseDto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LixoResponseDto criar(@RequestBody LixoRequestDto dados){
        return service.criar(dados);
    }

    @PutMapping("/{id}")
    public LixoResponseDto atualizar(@PathVariable Long id, @RequestBody LixoRequestDto dados){
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        service.deletar(id);
    }
}