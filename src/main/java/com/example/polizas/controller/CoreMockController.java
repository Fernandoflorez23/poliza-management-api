package com.example.polizas.controller;

import com.example.polizas.dto.CoreEventoRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    @PostMapping("/evento")
    public ResponseEntity<Map<String, String>> recibirEvento(@Valid @RequestBody CoreEventoRequest request) {
        log.info("Evento recibido del CORE -> evento: {}, polizaId: {}. Operacion registrada como intentada.",
                request.getEvento(), request.getPolizaId());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evento registrado correctamente",
                "evento", request.getEvento(),
                "polizaId", String.valueOf(request.getPolizaId())
        ));
    }
}
