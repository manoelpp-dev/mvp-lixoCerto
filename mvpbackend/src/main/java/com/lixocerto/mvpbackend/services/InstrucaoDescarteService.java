package com.lixocerto.mvpbackend.services;

import com.lixocerto.mvpbackend.model.InstrucaoDescarte;
import com.lixocerto.mvpbackend.repositories.InstrucaoDescarteRepository;
import com.lixocerto.mvpbackend.repositories.LixoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InstrucaoDescarteService {
    private final InstrucaoDescarteRepository repository;
    private final LixoRepository lixoRepository;

    public instrucaoDescarteService(InstrucaoDescarteRepository repository, LixoRepository lixoRepository) {
        this.repository = repository;
        this.lixoRepository = lixoRepository;
    }

    public List<InstrucaoDescarte> listar() {
        return repository.findAll();
    }

    public InstrucaoDescarte buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrução de descarte não encontrada"));
    }

    public InstrucaoDescarte criar(InstrucaoDescarte instrucao) {
        validar(instrucao);
        return repository.save(instrucao);

    }

    public InstrucaoDescarte atualizar(Long id, InstrucaoDescarte dados) {
        validar(dados);
        InstrucaoDescarte existente = buscarPorId(id);
        existente.setInstrucao(dados.getInstrucao());
        existente.setSugestaoReciclagem(dados.getSugestaoReciclagem());
        existente.setCorLixo(dados.getCorLixo());
        return repository.save(existente);
    }

    public void deletar(Long id) {
        InstrucaoDescarte existente = buscarPorId(id);
        boolean emUso = lixoRepository.findAll().stream().anyMatch(1 -> 1.getInstrucaoDescarte().getId().equals(id));
        if (emUso){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir: existem tipos de lixo com esta instrução de descarte.");
        }
        repository.delete(existente);

    }

    private void validar(InstrucaoDescarte i) {
        if (i.getInstrucao() == null || i.getInstrucao().isBlank()) {
            throw erro("A instrução é obrigatória");

        }

        if (i.getInstrucao().length() > 1000) {
            throw erro("A instrução não pode ter mais de 1000 caracteres");

        }

        if (i.getSugestaoReciclagem() != null && i.getSugestaoReciclagem().length() > 1000) {
            throw erro("A sugestão de reciclagem deve ter no máximo 1000 caracteres");
        }

        if (i.getCorLixo() != null && i.getCorLixo().length() > 50) {
            throw erro ("A cor do lixo deve ter no máximo 50 caracteres");
        }
    }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
