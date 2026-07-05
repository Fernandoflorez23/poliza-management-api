package com.example.polizas.controller;

import com.example.polizas.dto.RiesgoRequest;
import com.example.polizas.entity.EstadoPoliza;
import com.example.polizas.entity.Poliza;
import com.example.polizas.entity.Riesgo;
import com.example.polizas.entity.TipoPoliza;
import com.example.polizas.service.PolizaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
@RequiredArgsConstructor
public class PolizaController {

    private final PolizaService polizaService;

    @GetMapping
    public ResponseEntity<List<Poliza>> listar(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        return ResponseEntity.ok(polizaService.listar(tipo, estado));
    }

    @GetMapping("/{id}/riesgos")
    public ResponseEntity<List<Riesgo>> listarRiesgos(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.listarRiesgos(id));
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<Poliza> renovar(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.renovar(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Poliza> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.cancelar(id));
    }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<Riesgo> agregarRiesgo(@PathVariable Long id, @Valid @RequestBody RiesgoRequest request) {
        Riesgo riesgo = polizaService.agregarRiesgo(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(riesgo);
    }
}
