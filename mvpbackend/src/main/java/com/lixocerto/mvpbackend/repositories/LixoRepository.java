
package com.lixocerto.mvpbackend.repositories;

import com.lixocerto.mvpbackend.model.Lixo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LixoRepository extends JpaRepository<Lixo, Long> {

    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByInstrucaoDescarteId(Long instrucaoId);

    boolean existsByInstrucaoDescarteIdAndIdNot(Long instrucaoId, Long id);

    Optional<Lixo> findFirstByNomeContainingIgnoreCase(String nome);
}
