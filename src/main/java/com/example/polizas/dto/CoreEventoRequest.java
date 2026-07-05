package com.example.polizas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoreEventoRequest {

    @NotNull(message = "El campo evento es obligatorio")
    private String evento;

    @NotNull(message = "El campo polizaId es obligatorio")
    private Long polizaId;
}
