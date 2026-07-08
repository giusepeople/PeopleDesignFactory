package com.pdfactory.pdf_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "Ruoli")
@Getter
@Setter
@NoArgsConstructor
public class Ruolo
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String missione;

    @Column(columnDefinition = "TEXT")
    private String competenze;

    @Column(columnDefinition = "TEXT")
    private String superpoteri;

    @Column(name = "punti_critici", columnDefinition = "TEXT")
    private String puntiCritici;
}