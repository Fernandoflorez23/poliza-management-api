package com.example.polizas.service;

import com.example.polizas.entity.EstadoRiesgo;
import com.example.polizas.entity.Riesgo;
import com.example.polizas.exception.BusinessException;
import com.example.polizas.exception.ResourceNotFoundException;
import com.example.polizas.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;

    public Riesgo buscarPorId(Long id) {
        return riesgoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Riesgo no encontrado con id: " + id));
    }

    @Transactional
    public Riesgo cancelar(Long id) {
        Riesgo riesgo = buscarPorId(id);

        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new BusinessException("El riesgo ya se encuentra cancelado");
        }

        riesgo.setEstado(EstadoRiesgo.CANCELADO);
        return riesgoRepository.save(riesgo);
    }
}
