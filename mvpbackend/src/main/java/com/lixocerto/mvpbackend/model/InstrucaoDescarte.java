package com.lixocerto.mvpbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "instrucao_descarte")
public class InstrucaoDescarte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String instrucao;

    @Column(name = "sugestao_reciclagem", length = 1000)
    private String sugestaoReciclagem;

    @Column(name = "cor_lixo", length = 50)
    private String corLixo;

    public Long getId() { return id; }
    public String getInstrucao() { return instrucao; }
    public void setInstrucao(String instrucao) { this.instrucao = instrucao; }
    public String getSugestaoReciclagem() { return sugestaoReciclagem; }
    public void setSugestaoReciclagem(String sugestaoReciclagem) { this.sugestaoReciclagem = sugestaoReciclagem; }
    public String getCorLixo() { return corLixo; }
    public void setCorLixo(String corLixo) { this.corLixo = corLixo; }
}