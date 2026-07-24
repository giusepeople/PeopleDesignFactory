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

    @Column(name = "contenuto_testo", columnDefinition = "TEXT")
    private String contenutoTesto;

    @Column(name = "dati_json", columnDefinition = "TEXT")
    private String datiJson;

    // opzioni decisionali (es. Livello 2: opzione A/B/C con costo, tempo, rischio)
    @Column(name = "opzioni_json", columnDefinition = "TEXT")
    private String opzioniJson;

    // evento opzionale che si attiva dopo N minuti dall'inizio fase (o manualmente dal GM)
    @Column(name = "complicazione_testo", columnDefinition = "TEXT")
    private String complicazioneTesto;

    @Column(name = "complicazione_dopo_minuti")
    private Integer complicazioneDopoMinuti;

    @Column(name = "complicazione_minuti_extra")
    private Integer complicazioneMinutiExtra;
}