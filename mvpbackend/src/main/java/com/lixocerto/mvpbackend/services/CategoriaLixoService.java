package com.lixocerto.mvpbackend.services;

import com.lixocerto.mvpbackend.model.CategoriaLixo;
import com.lixocerto.mvpbackend.repositories.CategoriaLixoRepository;
import com.lixocerto.mvpbackend.repositories.LixoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service 
public class CategoriaLixoService {

    private final CategoriaLixoRepository repository;
    private final LixoRepository lixoRepository;

    public CategoriaLixoService(CategoriaLixoRepository repository,
                                LixoRepository lixoRepository) {
        this.repository = repository;
        this.lixoRepository = lixoRepository;
    }

    public List<CategoriaLixo> listar() {
        return repository.findAll();
    
    }

    public CategoriaLixo buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException( HttpStatus.NOT_FOUND, "Categoria não encontrada"));

    }

    public CategoriaLixo criar(CategoriaLixo categoria) {
        validar(categoria);
        return repository.save(categoria);
    }

    public CategoriaLixo atualizar (Long id, CategoriaLixo dados) {
        validar(dados);
        CategoriaLixo existente = buscarPorId(id);
        existente.setDescricao(dados.getDescricao());
        existente.setTipo_de_lixo(dados.getTipo_de_lixo());
        return repository.save(existente);
    }

    public void deletar(Long id) {
        CategoriaLixo existente = buscarPorId(id);
        boolean emUso = lixoRepository.findAll().stream().anyMatch(1 -> 1.getCategoria().getId().equals(id));
        if (emUso) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir: existem tipos de lixo nesta categoria.");
        }
        repository.delete(existente);
    }

    private void validar(CategoriaLixo categoria) {
        String descricao = categoria.getDescricao();
        if(descricao == null || descricao.isBlank()) {
            throw erro("A descrição é obrigatória");

        }
        if (descricao.length() > 100) {
            throw erro("A descrição deve ter no máximo 100 caracteres");

        }
        String tipo = categoria.getTipo_de_lixo();
        if (tipo == null || tipo.isBlank()) {
            throw erro("O tipo de lixo é obrigatório");
        }
        if (tipo.length() > 30) {
            throw erro("O tipo de lixo deve ter no máximo 30 caracteres");
        }
    }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
