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

    @Column(nullable = false, length = 30)
    private String tipo_de_lixo;

    public Long getId() { return id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipo_de_lixo() { return tipo_de_lixo; }
    public void setTipo_de_lixo(String tipo_de_lixo) {this.tipo_de_lixo = tipo_de_lixo;}




}