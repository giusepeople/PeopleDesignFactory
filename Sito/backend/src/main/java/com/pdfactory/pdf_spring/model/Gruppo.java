package com.pdfactory.pdf_spring.model;

import com.pdfactory.pdf_spring.enums.StatoTeam;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "Gruppo")
public class Gruppo
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_partita", nullable = false)
    private Partita partita;

    @Column(name = "team_num", nullable = false)
    private Integer teamNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoTeam stato = StatoTeam.LAVORANDO;

    @OneToMany(mappedBy = "gruppo")
    private List<Giocatore> giocatori = new ArrayList<>();

    @OneToMany(mappedBy = "gruppo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvioModulo> moduliInviati = new ArrayList<>();
}
