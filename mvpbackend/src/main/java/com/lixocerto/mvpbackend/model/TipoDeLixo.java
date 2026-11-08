package com.lixocerto.mvpbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_de_lixo")
public class TipoDeLixo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String descricao;

    // cada tipo tem exatamente uma instrução (FK única)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrucao_descarte_id", nullable = false, unique = true)
    private InstrucaoDescarte instrucaoDescarte;

    // cada tipo pertence a uma categoria; a categoria pode ter vários tipos
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_lixo_id", nullable = false)
    private CategoriaLixo categoria;

    public Long getId() { return id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public InstrucaoDescarte getInstrucaoDescarte() { return instrucaoDescarte; }
    public void setInstrucaoDescarte(InstrucaoDescarte instrucaoDescarte) { this.instrucaoDescarte = instrucaoDescarte; }
    public CategoriaLixo getCategoria() { return categoria; }
    public void setCategoria(CategoriaLixo categoria) { this.categoria = categoria; }
}