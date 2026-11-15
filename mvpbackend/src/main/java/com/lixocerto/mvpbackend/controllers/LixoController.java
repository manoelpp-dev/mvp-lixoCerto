
package com.lixocerto.mvpbackend.controllers;

import com.lixocerto.mvpbackend.dto.LixoDetalheDTO;
import com.lixocerto.mvpbackend.dto.LixoRequestDto;
import com.lixocerto.mvpbackend.dto.LixoResponseDto;
import com.lixocerto.mvpbackend.services.IdentificacaoFotoService;
import com.lixocerto.mvpbackend.services.LixoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/lixos")
@CrossOrigin(origins = "*")
public class LixoController {

    private final LixoService service;
    private final IdentificacaoFotoService identificacaoFotoService;

    public LixoController(
            LixoService service,
            IdentificacaoFotoService identificacaoFotoService) {
        this.service = service;
        this.identificacaoFotoService = identificacaoFotoService;
    }

    // CRUD - Lixos

    @GetMapping
    public List<LixoResponseDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public LixoResponseDto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LixoResponseDto criar(@RequestBody LixoRequestDto dados) {
        return service.criar(dados);
    }

    @PutMapping("/{id}")
    public LixoResponseDto atualizar(
            @PathVariable Long id,
            @RequestBody LixoRequestDto dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    // Identificação por foto

    @PostMapping(
            value = "/identificar-foto",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public LixoDetalheDTO identificarPorFoto(
            @RequestParam("foto") MultipartFile foto) {
        return identificacaoFotoService.identificarPorFoto(foto);
    }
}
