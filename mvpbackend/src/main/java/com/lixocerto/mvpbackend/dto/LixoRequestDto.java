package com.lixocerto.mvpbackend.dto;

public record LixoRequestDto(
        String nome,
        String descricao,
        Long categoriaId,
        Long instrucaoDescarteId,
        String palavrasChave,
        Boolean umido
) {
}
