package com.example.polizas.repository;

import com.example.polizas.entity.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {

    List<Riesgo> findByPolizaId(Long polizaId);
}
