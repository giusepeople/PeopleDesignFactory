package com.pdfactory.pdf_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(
        name = "risposta",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_risposta_invio_domanda",
                columnNames = {"invio_modulo_id", "domanda_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Risposta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invio_modulo_id", nullable = false)
    private InvioModulo invioModulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domanda_id", nullable = false)
    private Domanda domanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risposto_da_giocatore_id")
    private Giocatore rispostoDa;

    @Column(name = "testo_risposta", columnDefinition = "TEXT")
    private String testoRisposta;

    // motivazione testuale abbinata (es. giustificazione della scelta multipla)
    @Column(name = "giustificazione", columnDefinition = "TEXT")
    private String giustificazione;

    @Column(name = "hint_usato", nullable = false)
    private Boolean hintUsato = false;

    @Column(name = "corretta")
    private Boolean corretta;
}