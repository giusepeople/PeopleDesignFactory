package com.pdfactory.pdf_spring.model;

import com.pdfactory.pdf_spring.enums.StatoGioco;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Partita")
public class Partita
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 10)
    private String codPartita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameMaster_id", nullable = false)
    private GameMaster gameMaster;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoGioco status = StatoGioco.IN_ATTESA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_fase_id")
    private Fase faseAttuale;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "fase_iniziata_il")
    private Instant faseIniziataIl;

    @Column(name = "complicazione_attivata", nullable = false)
    private Boolean complicazioneAttivata = false;

    @OneToMany(mappedBy = "partita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gruppo> gruppi = new ArrayList<>();

    @OneToMany(mappedBy = "partita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Giocatore> giocatori = new ArrayList<>();
}