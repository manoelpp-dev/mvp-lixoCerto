package com.lixocerto.mvpbackend.services;

import com.lixocerto.mvpbackend.dto.LixoRequestDto;
import com.lixocerto.mvpbackend.dto.LixoResponseDto;
import com.lixocerto.mvpbackend.model.CategoriaLixo;
import com.lixocerto.mvpbackend.model.InstrucaoDescarte;
import com.lixocerto.mvpbackend.model.Lixo;
import com.lixocerto.mvpbackend.repositories.CategoriaLixoRepository;
import com.lixocerto.mvpbackend.repositories.InstrucaoDescarteRepository;
import com.lixocerto.mvpbackend.repositories.LixoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LixoService {

    private final LixoRepository repository;
    private final CategoriaLixoRepository categoriaRepository;
    private final InstrucaoDescarteRepository instrucaoRepository;

    public LixoService(LixoRepository repository,
                       CategoriaLixoRepository categoriaRepository,
                       InstrucaoDescarteRepository instrucaoRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
        this.instrucaoRepository = instrucaoRepository;
    }

    /** Filtros opcionais, usados pelos selects de apoio do front (categoria/material e seco/úmido). */
    public List<LixoResponseDto> listar(Long categoriaId, Boolean umido) {
        return repository.findAll().stream()
                .filter(l -> categoriaId == null || l.getCategoria().getId().equals(categoriaId))
                .filter(l -> umido == null || umido.equals(l.getUmido()))
                .map(LixoResponseDto::from)
                .toList();
    }

    public LixoResponseDto buscarPorId(Long id) {
        return LixoResponseDto.from(obter(id));
    }

    @Transactional
    public LixoResponseDto criar(LixoRequestDto dados) {
        validar(dados);
        CategoriaLixo categoria = obterCategoria(dados.categoriaId());
        InstrucaoDescarte instrucao = obterInstrucao(dados.instrucaoDescarteId());

        if (repository.existsByInstrucaoDescarteId(instrucao.getId())) {
            throw conflitoInstrucao();
        }

        Lixo lixo = new Lixo();
        aplicar(lixo, dados, categoria, instrucao);
        return LixoResponseDto.from(repository.save(lixo));
    }

    @Transactional
    public LixoResponseDto atualizar(Long id, LixoRequestDto dados) {
        Lixo existente = obter(id);
        validar(dados);
        CategoriaLixo categoria = obterCategoria(dados.categoriaId());
        InstrucaoDescarte instrucao = obterInstrucao(dados.instrucaoDescarteId());

        if (repository.existsByInstrucaoDescarteIdAndIdNot(instrucao.getId(), id)) {
            throw conflitoInstrucao();
        }

        aplicar(existente, dados, categoria, instrucao);
        return LixoResponseDto.from(repository.save(existente));
    }

    @Transactional
    public void deletar(Long id) {
        repository.delete(obter(id));
    }

    // ---------- auxiliares ----------

    private Lixo obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lixo não encontrado"));
    }

    private CategoriaLixo obterCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }

    private InstrucaoDescarte obterInstrucao(Long id) {
        return instrucaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrução de descarte não encontrada"));
    }

    private void aplicar(Lixo lixo, LixoRequestDto d, CategoriaLixo categoria, InstrucaoDescarte instrucao) {
        lixo.setNome(d.nome().trim());
        lixo.setDescricao(d.descricao().trim());
        lixo.setPalavrasChave(d.palavrasChave() == null || d.palavrasChave().isBlank() ? null : d.palavrasChave().trim());
        lixo.setUmido(d.umido());
        lixo.setCategoria(categoria);
        lixo.setInstrucaoDescarte(instrucao);
    }

    private void validar(LixoRequestDto d) {
        if (d == null) throw erro("Corpo da requisição é obrigatório");
        if (d.nome() == null || d.nome().isBlank()) throw erro("O nome é obrigatório");
        if (d.nome().length() > 50) throw erro("O nome deve ter no máximo 50 caracteres");
        if (d.descricao() == null || d.descricao().isBlank()) throw erro("A descrição é obrigatória");
        if (d.descricao().length() > 200) throw erro("A descrição deve ter no máximo 200 caracteres");
        if (d.palavrasChave() != null && d.palavrasChave().length() > 1000) {
            throw erro("As palavras-chave devem ter no máximo 1000 caracteres");
        }
        if (d.categoriaId() == null) throw erro("A categoria é obrigatória");
        if (d.instrucaoDescarteId() == null) throw erro("A instrução de descarte é obrigatória");
    }

    private ResponseStatusException conflitoInstrucao() {
        return new ResponseStatusException(HttpStatus.CONFLICT,
                "Esta instrução de descarte já está vinculada a outro lixo");
    }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
