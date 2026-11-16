package com.lixocerto.mvpbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Lixo")
public class Lixo {

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

    @Column(nullable = false, length = 50)
    private String nome;

    // Palavras-chave/sinônimos separados por vírgula (usadas na busca por texto)
    @Column(name = "palavras_chave", length = 1000)
    private String palavrasChave;

    // true = úmido/molhado, false = seco, null = não se aplica (select de apoio no front)
    private Boolean umido;

    public Long getId() { return id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public InstrucaoDescarte getInstrucaoDescarte() { return instrucaoDescarte; }
    public void setInstrucaoDescarte(InstrucaoDescarte instrucaoDescarte) { this.instrucaoDescarte = instrucaoDescarte; }
    public CategoriaLixo getCategoria() { return categoria; }
    public void setCategoria(CategoriaLixo categoria) { this.categoria = categoria; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPalavrasChave() { return palavrasChave; }
    public void setPalavrasChave(String palavrasChave) { this.palavrasChave = palavrasChave; }
    public Boolean getUmido() { return umido; }
    public void setUmido(Boolean umido) { this.umido = umido; }
}
