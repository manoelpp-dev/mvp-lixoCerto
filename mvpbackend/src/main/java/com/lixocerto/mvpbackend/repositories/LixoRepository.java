package com.lixocerto.mvpbackend.repositories;

import com.lixocerto.mvpbackend.model.Lixo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LixoRepository extends JpaRepository<Lixo, Long> {

    boolean existsByInstrucaoDescarteId(Long id);

    Optional<Lixo> findFirstByNomeContainingIgnoreCase(String nome);
}
