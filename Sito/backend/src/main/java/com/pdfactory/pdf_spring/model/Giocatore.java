package com.pdfactory.pdf_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "Giocatore")
@Getter
@Setter
@NoArgsConstructor
public class Giocatore
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_partita", nullable = false)
    private Partita partita;

    // nullable finche' il GM non assegna i gruppi in lobby
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gruppo_id")
    private Gruppo gruppo;

    // nullable finche' non assegnato; puo' cambiare (scambio PM in Turbativa 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Ruolo ruolo;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "session_token", nullable = false, unique = true)
    private String sessionToken;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();
}
