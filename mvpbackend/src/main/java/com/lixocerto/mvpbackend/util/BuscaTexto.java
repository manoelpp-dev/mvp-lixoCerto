package com.lixocerto.mvpbackend.util;

import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** Funções puras (sem Spring) usadas na busca de lixos por texto livre. */
public final class BuscaTexto {

    public static final int PESO_PALAVRA_CHAVE = 2;
    public static final int PESO_DESCRICAO = 1;

    // Palavras sem valor para a busca (já sem acento e em minúsculas). Edite à vontade.
    private static final Set<String> STOPWORDS = Set.of(
            "de", "da", "do", "das", "dos", "um", "uma", "em", "no", "na", "nos", "nas",
            "para", "por", "com", "que", "ja", "recem", "tomada", "tomado", "meu", "minha",
            "isso", "esse", "essa", "este", "esta", "tipo", "coisa",
            "velho", "velha", "velhos", "velhas", "antigo", "antiga", "usado", "usada", "quebrado", "quebrada");

    private BuscaTexto() {}

    /** Minúsculas, sem acento, sem pontuação: "Garrafa PET, recém-tomada!" -> "garrafa pet recem tomada". */
    public static String normalizar(String texto) {
        if (texto == null) return "";
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return semAcento.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    }

    /** Palavras relevantes do texto, normalizadas, sem stopwords e no singular simples. */
    public static Set<String> palavras(String texto) {
        Set<String> resultado = new LinkedHashSet<>();
        for (String p : normalizar(texto).split(" ")) {
            if (p.length() < 2 || STOPWORDS.contains(p)) continue;
            resultado.add(singular(p));
        }
        return resultado;
    }

    /**
     * Pontua um lixo para a consulta: cada palavra da consulta vale
     * PESO_PALAVRA_CHAVE se estiver nas palavras-chave, ou PESO_DESCRICAO se só estiver na descrição.
     */
    public static int pontuar(Set<String> consulta, String palavrasChave, String descricao) {
        Set<String> chaves = palavras(palavrasChave);
        Set<String> daDescricao = palavras(descricao);
        int total = 0;
        for (String palavra : consulta) {
            if (chaves.contains(palavra)) total += PESO_PALAVRA_CHAVE;
            else if (daDescricao.contains(palavra)) total += PESO_DESCRICAO;
        }
        return total;
    }

    private static String singular(String p) {
        return (p.length() > 3 && p.endsWith("s")) ? p.substring(0, p.length() - 1) : p;
    }
}
