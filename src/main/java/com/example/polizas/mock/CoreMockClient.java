package com.example.polizas.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Cliente que simula el envio de eventos al sistema CORE externo.
 * En este ejercicio unicamente registra en logs que la operacion fue intentada.
 */
@Slf4j
@Component
public class CoreMockClient {

    public void notificarActualizacion(Long polizaId) {
        log.info("Enviando evento ACTUALIZACION al CORE para la poliza con id: {}", polizaId);
    }
}
