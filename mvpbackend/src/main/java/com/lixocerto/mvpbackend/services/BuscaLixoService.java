package com.lixocerto.mvpbackend.services;

import com.lixocerto.mvpbackend.dto.BuscaLixoResponseDto;
import com.lixocerto.mvpbackend.dto.LixoResponseDto;
import com.lixocerto.mvpbackend.model.Lixo;
import com.lixocerto.mvpbackend.repositories.LixoRepository;
import com.lixocerto.mvpbackend.util.BuscaTexto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.Set;


@Service
public class BuscaLixoService {

    private static final int MAX_RESULTADOS = 5;
    private static final int MAX_TEXTO = 200;

    private final LixoRepository lixoRepository;

    public BuscaLixoService(LixoRepository lixoRepository) {
        this.lixoRepository = lixoRepository;
    }

    @Transactional(readOnly = true)
    public BuscaLixoResponseDto buscar(String q) {
        if (q == null || q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe a descrição do resíduo");
        }
        if (q.length() > MAX_TEXTO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A descrição deve ter no máximo " + MAX_TEXTO + " caracteres");
        }

        Set<String> consulta = BuscaTexto.palavras(q);

        var resultados = lixoRepository.findAll().stream()
                .map(l -> new Pontuado(l, BuscaTexto.pontuar(consulta, l.getPalavrasChave(), l.getNome() + " " + l.getDescricao())))
                .filter(p -> p.pontos() > 0)
                .sorted(Comparator.comparingInt(Pontuado::pontos).reversed()
                        .thenComparing(p -> p.lixo().getNome()))
                .limit(MAX_RESULTADOS)
                .map(p -> new BuscaLixoResponseDto.Resultado(p.pontos(), LixoResponseDto.from(p.lixo())))
                .toList();

        return new BuscaLixoResponseDto(q.trim(), resultados);
    }

    private record Pontuado(Lixo lixo, int pontos) {}
}
