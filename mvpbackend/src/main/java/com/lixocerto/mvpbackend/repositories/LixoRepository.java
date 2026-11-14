package com.lixocerto.mvpbackend.repositories;

import com.lixocerto.mvpbackend.model.Lixo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LixoRepository extends JpaRepository<Lixo, Long> {
    boolean existsByCategoriaId(Long categoriaId);
    boolean existsByInstrucaoDescarteId(Long instrucaoId);

    boolean existsByInstrucaoDescarteIdAndIdNot(Long instrucaoId, Long id);
}


