package com.lixocerto.mvpbackend.dto;

public record LixoDetalheDTO(
        Long id,
        String nome,
        String descricao,
        String categoria,
        String corLixo,
        String instrucaoPreparo,
        String sugestaoReutilizacao
) {}
