package com.example.polizas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numeroPoliza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal canon;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal prima;

    @Builder.Default
    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riesgo> riesgos = new ArrayList<>();

    public void addRiesgo(Riesgo riesgo) {
        riesgos.add(riesgo);
        riesgo.setPoliza(this);
    }
}
