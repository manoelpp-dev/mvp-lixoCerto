package com.lixocerto.mvpbackend.dto;

import java.util.List;

/** resultados vazio = nada encontrado (o front deve oferecer os selects de apoio ou o envio de foto). */
public record BuscaLixoResponseDto(String consulta, List<Resultado> resultados) {

    public record Resultado(int pontuacao, LixoResponseDto lixo) {}
}
