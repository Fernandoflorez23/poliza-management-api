package com.example.polizas.service;

import com.example.polizas.dto.RiesgoRequest;
import com.example.polizas.entity.*;
import com.example.polizas.exception.BusinessException;
import com.example.polizas.exception.ResourceNotFoundException;
import com.example.polizas.mock.CoreMockClient;
import com.example.polizas.repository.PolizaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final CoreMockClient coreMockClient;

    @Value("${app.business.ipc}")
    private BigDecimal ipc;

    /**
     * Lista polizas aplicando filtros opcionales por tipo y estado.
     */
    public List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado) {
        if (tipo != null && estado != null) {
            return polizaRepository.findByTipoAndEstado(tipo, estado);
        }
        if (tipo != null) {
            return polizaRepository.findByTipo(tipo);
        }
        if (estado != null) {
            return polizaRepository.findByEstado(estado);
        }
        return polizaRepository.findAll();
    }

    public Poliza buscarPorId(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poliza no encontrada con id: " + id));
    }

    public List<Riesgo> listarRiesgos(Long polizaId) {
        return buscarPorId(polizaId).getRiesgos();
    }

    /**
     * Renueva una poliza: incrementa canon y prima en +IPC y cambia el estado a RENOVADA.
     * Regla: no se puede renovar una poliza cancelada.
     */
    @Transactional
    public Poliza renovar(Long id) {
        Poliza poliza = buscarPorId(id);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No se puede renovar una poliza cancelada");
        }

        BigDecimal factorIncremento = BigDecimal.ONE.add(ipc);

        poliza.setCanon(poliza.getCanon().multiply(factorIncremento).setScale(2, RoundingMode.HALF_UP));
        poliza.setPrima(poliza.getPrima().multiply(factorIncremento).setScale(2, RoundingMode.HALF_UP));
        poliza.setEstado(EstadoPoliza.RENOVADA);

        Poliza actualizada = polizaRepository.save(poliza);
        coreMockClient.notificarActualizacion(actualizada.getId());
        return actualizada;
    }

    /**
     * Cancela una poliza y en cascada cancela todos sus riesgos asociados.
     */
    @Transactional
    public Poliza cancelar(Long id) {
        Poliza poliza = buscarPorId(id);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("La poliza ya se encuentra cancelada");
        }

        poliza.setEstado(EstadoPoliza.CANCELADA);
        poliza.getRiesgos().forEach(riesgo -> riesgo.setEstado(EstadoRiesgo.CANCELADO));

        Poliza actualizada = polizaRepository.save(poliza);
        coreMockClient.notificarActualizacion(actualizada.getId());
        return actualizada;
    }

    /**
     * Agrega un riesgo a una poliza.
     * Reglas:
     *  - Solo valido si el tipo de poliza es COLECTIVA.
     *  - No se pueden agregar riesgos a una poliza cancelada.
     *
     * Nota de diseño: la regla "una poliza INDIVIDUAL solo puede tener 1 riesgo"
     * se garantiza por construccion en este endpoint, ya que unicamente las polizas
     * COLECTIVA pueden recibir riesgos adicionales via API. Una poliza INDIVIDUAL
     * se crea siempre con su unico riesgo y nunca pasa por este metodo, por lo que
     * no requiere una validacion de conteo adicional en tiempo de ejecucion.
     */
    @Transactional
    public Riesgo agregarRiesgo(Long polizaId, RiesgoRequest request) {
        Poliza poliza = buscarPorId(polizaId);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No se pueden agregar riesgos a una poliza cancelada");
        }

        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new BusinessException("Solo se pueden agregar riesgos a polizas de tipo COLECTIVA");
        }

        Riesgo riesgo = Riesgo.builder()
                .descripcion(request.getDescripcion())
                .estado(EstadoRiesgo.ACTIVO)
                .build();

        poliza.addRiesgo(riesgo);
        polizaRepository.save(poliza);

        return riesgo;
    }
}
