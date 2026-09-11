package com.pdfactory.pdf_spring.model;

import com.pdfactory.pdf_spring.enums.StatoInvio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "invio_modulo")
@Getter
@Setter
@NoArgsConstructor
public class InvioModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gruppo_id", nullable = false)
    private Gruppo gruppo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviato_da_giocatore_id")
    private Giocatore inviatoDa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoInvio stato = StatoInvio.BOZZA;

    @Column(name = "inviato_il")
    private Instant inviatoIl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisionato_da_gm_id")
    private GameMaster revisionatoDa;

    @Column(name = "revisionato_il")
    private Instant revisionatoIl;

    @Column(name = "motivo_rifiuto", columnDefinition = "TEXT")
    private String motivoRifiuto;

    @Column(name = "minuti_extra")
    private Integer minutiExtra = 0;

    @OneToMany(mappedBy = "invioModulo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Risposta> risposte = new ArrayList<>();
}