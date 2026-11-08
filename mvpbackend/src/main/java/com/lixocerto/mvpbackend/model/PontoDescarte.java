package com.lixocerto.mvpbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ponto_descarte")
public class PontoDescarte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    // não está no diagrama: é a descrição que falta no site da Prefeitura
    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(length = 150)
    private String rua;

    private Integer num;

    @Column(length = 9)
    private String cep;

    @Column(length = 100)
    private String bairro;

    // opcionais, para mostrar o ponto no mapa
    private Double latitude;
    private Double longitude;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }
    public Integer getNum() { return num; }
    public void setNum(Integer num) { this.num = num; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}