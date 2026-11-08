package com.lixocerto.mvpbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria_lixo")
public class CategoriaLixo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String descricao;

    public Long getId() { return id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}