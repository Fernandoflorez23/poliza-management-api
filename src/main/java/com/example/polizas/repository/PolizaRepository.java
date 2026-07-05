package com.example.polizas.repository;

import com.example.polizas.entity.EstadoPoliza;
import com.example.polizas.entity.Poliza;
import com.example.polizas.entity.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long>, JpaSpecificationExecutor<Poliza> {

    List<Poliza> findByTipoAndEstado(TipoPoliza tipo, EstadoPoliza estado);

    List<Poliza> findByTipo(TipoPoliza tipo);

    List<Poliza> findByEstado(EstadoPoliza estado);
}
