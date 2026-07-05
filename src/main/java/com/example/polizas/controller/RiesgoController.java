package com.example.polizas.controller;

import com.example.polizas.entity.Riesgo;
import com.example.polizas.service.RiesgoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/riesgos")
@RequiredArgsConstructor
public class RiesgoController {

    private final RiesgoService riesgoService;

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Riesgo> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(riesgoService.cancelar(id));
    }
}
