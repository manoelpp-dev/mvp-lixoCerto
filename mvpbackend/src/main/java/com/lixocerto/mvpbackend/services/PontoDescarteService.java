package com.lixocerto.mvpbackend.services;

import com.lixocerto.mvpbackend.model.PontoDescarte;
import com.lixocerto.mvpbackend.repositories.PontoDescarteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PontoDescarteService {

    private static final String STATUS_ATIVO = "ATIVO";
    private static final String STATUS_INATIVO = "INATIVO";

    private final PontoDescarteRepository repository;

    public PontoDescarteService(PontoDescarteRepository repository) {
        this.repository = repository;
    }

    public List<PontoDescarte> listar() {
        return repository.findAll();
    }

    public PontoDescarte buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ponto de descarte não encontrado"));
    }

    @Transactional
    public PontoDescarte criar(PontoDescarte ponto) {
        validar(ponto);
        PontoDescarte novo = new PontoDescarte();
        copiar(novo, ponto);
        return repository.save(novo);
    }

    @Transactional
    public PontoDescarte atualizar(Long id, PontoDescarte dados) {
        PontoDescarte existente = buscarPorId(id);
        validar(dados);
        copiar(existente, dados);
        return repository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        repository.delete(buscarPorId(id));
    }

    private void copiar(PontoDescarte destino, PontoDescarte origem) {
        destino.setNome(origem.getNome().trim());
        destino.setDescricao(origem.getDescricao());
        destino.setCidade(origem.getCidade().trim());
        destino.setRua(origem.getRua());
        destino.setNum(origem.getNum());
        destino.setCep(origem.getCep());
        destino.setBairro(origem.getBairro());
        destino.setLatitude(origem.getLatitude());
        destino.setLongitude(origem.getLongitude());
    }

    private void validar(PontoDescarte p) {
        if (p == null) throw erro("Corpo da requisição é obrigatório");
        if (p.getNome() == null || p.getNome().isBlank()) throw erro("O nome é obrigatório");
        if (p.getNome().length() > 150) throw erro("O nome deve ter no máximo 150 caracteres");
        if (p.getCidade() == null || p.getCidade().isBlank()) throw erro("A cidade é obrigatória");
        if (p.getCidade().length() > 100) throw erro("A cidade deve ter no máximo 100 caracteres");
        if (p.getDescricao() != null && p.getDescricao().length() > 1000) {
            throw erro("A descrição deve ter no máximo 1000 caracteres");
        }
        if (p.getRua() != null && p.getRua().length() > 150) throw erro("A rua deve ter no máximo 150 caracteres");
        if (p.getBairro() != null && p.getBairro().length() > 100) throw erro("O bairro deve ter no máximo 100 caracteres");
        if (p.getCep() != null && !p.getCep().isBlank() && !p.getCep().matches("\\d{5}-?\\d{3}")) {
            throw erro("CEP inválido (use o formato 92010-000)");
        }
        if (p.getLatitude() != null && (p.getLatitude() < -90 || p.getLatitude() > 90)) {
            throw erro("Latitude deve estar entre -90 e 90");
        }
        if (p.getLongitude() != null && (p.getLongitude() < -180 || p.getLongitude() > 180)) {
            throw erro("Longitude deve estar entre -180 e 180");
        }
        if (p.getStatus() != null && !p.getStatus().isBlank()
                && !STATUS_ATIVO.equalsIgnoreCase(p.getStatus().trim())
                && !STATUS_INATIVO.equalsIgnoreCase(p.getStatus().trim())) {
            throw erro("Status deve ser ATIVO ou INATIVO");
        }
    }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
