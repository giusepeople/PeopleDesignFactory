package com.pdfactory.pdf_spring.model;

import com.pdfactory.pdf_spring.enums.TipoFase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name = "Fase")
@Getter
@Setter
@NoArgsConstructor
public class Fase
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Integer ordinal;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoFase tipo;

    @Column(name = "default_duration_minutes", nullable = false)
    private Integer defaultDurataMinuti;
}