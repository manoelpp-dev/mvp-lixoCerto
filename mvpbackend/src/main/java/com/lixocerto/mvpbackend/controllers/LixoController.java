package com.lixocerto.mvpbackend.controllers;

import com.lixocerto.mvpbackend.dto.LixoDetalheDTO;
import com.lixocerto.mvpbackend.services.IdentificacaoFotoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/lixos")
@CrossOrigin(origins = "*")
public class LixoController {

    private final IdentificacaoFotoService identificacaoFotoService;

    public LixoController(IdentificacaoFotoService identificacaoFotoService) {
        this.identificacaoFotoService = identificacaoFotoService;
    }

    @PostMapping(value = "/identificar-foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LixoDetalheDTO identificarPorFoto(@RequestParam("foto") MultipartFile foto) {
        return identificacaoFotoService.identificarPorFoto(foto);
    }
}
