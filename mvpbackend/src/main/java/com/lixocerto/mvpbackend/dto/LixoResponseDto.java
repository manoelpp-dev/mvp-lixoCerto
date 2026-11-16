package com.lixocerto.mvpbackend.dto;

import com.lixocerto.mvpbackend.model.Lixo;

public record LixoResponseDto(
        Long id,
        String nome,
        String descricao,
        CategoriaResumo categoria,
        InstrucaoResumo instrucaoDescarte
) {
    public record CategoriaResumo(Long id, String descricao, String tipo_de_lixo){
    }
    public record InstrucaoResumo(Long id, String instrucao, String sugestaoReciclagem, String corLixo){
    }

    public static LixoResponseDto from(Lixo lixo){
        var c = lixo.getCategoria();
        var i = lixo.getInstrucaoDescarte();
        return new LixoResponseDto(
                lixo.getId(),
                lixo.getNome(),
                lixo.getDescricao(),
                new CategoriaResumo(c.getId(), c.getDescricao(), c.getTipo_de_lixo()),
                new InstrucaoResumo(i.getId(), i.getInstrucao(), i.getSugestaoReciclagem(), i.getCorLixo())
        );
    }
}
