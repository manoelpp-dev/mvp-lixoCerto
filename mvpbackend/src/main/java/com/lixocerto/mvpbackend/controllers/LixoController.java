package com.lixocerto.mvpbackend.controllers;

import com.lixocerto.mvpbackend.dto.BuscaLixoResponseDto;
import com.lixocerto.mvpbackend.dto.LixoDetalheDTO;
import com.lixocerto.mvpbackend.dto.LixoRequestDto;
import com.lixocerto.mvpbackend.dto.LixoResponseDto;
import com.lixocerto.mvpbackend.services.BuscaLixoService;
import com.lixocerto.mvpbackend.services.IdentificacaoFotoService;
import com.lixocerto.mvpbackend.services.LixoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/lixos")
public class LixoController {

    private final LixoService service;
    private final BuscaLixoService buscaService;
    private final IdentificacaoFotoService identificacaoFotoService;

    public LixoController(
            LixoService service,
            BuscaLixoService buscaService,
            IdentificacaoFotoService identificacaoFotoService) {
        this.service = service;
        this.buscaService = buscaService;
        this.identificacaoFotoService = identificacaoFotoService;
    }

    // ---------- Consulta ----------

    /** Filtros opcionais (selects de apoio): /api/lixos?categoriaId=1&umido=false */
    @GetMapping
    public List<LixoResponseDto> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Boolean umido) {
        return service.listar(categoriaId, umido);
    }

    /** Busca por texto, resolvida pelo sistema (sem IA): /api/lixos/busca?q=garrafa pet recém-tomada */
    @GetMapping("/busca")
    public BuscaLixoResponseDto buscar(@RequestParam String q) {
        return buscaService.buscar(q);
    }

    @GetMapping("/{id}")
    public LixoResponseDto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // ---------- CRUD ----------

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

    // ---------- Identificação por foto (IA) ----------

    @PostMapping(
            value = "/identificar-foto",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public LixoDetalheDTO identificarPorFoto(
            @RequestParam("foto") MultipartFile foto) {
        return identificacaoFotoService.identificarPorFoto(foto);
    }
}
